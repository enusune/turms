<template>
    <div
        class="content-online-user"
    >
        <div>
            <custom-input
                v-model:value="ids"
                :placeholder="$t('userId')"
                class="search-filter search-filter-id"
                :only-number-and-comma="true"
            />
            <a-button
                type="primary"
                class="search-button"
                :disabled="!ids"
                @click="search"
            >
                {{ $t('search') }}
            </a-button>
        </div>
        <div>
            <a-button
                type="primary"
                class="search-button"
                @click="setSelectedDevicesOffline"
            >
                {{ $t('disconnectSelectedDevices') }}
            </a-button>
        </div>
        <a-table
            bordered
            class="content-table"
            size="middle"
            :columns="columns"
            :data-source="records"
            :loading="loading"
            :scroll="{ y: scrollMaxHeight }"
            :row-selection="rowSelection"
            :pagination="false"
        >
            <template
                #operation="{ record }"
            >
                <div class="editable-row-operations">
                    <span v-if="!['OFFLINE', 'NONEXISTENT'].includes(record.userStatus)">
                        <a-popconfirm
                            :title="$t('confirmDisconnect')"
                            @confirm="() => disconnectUsers([record.id], [record.deviceType])"
                        >
                            <a>{{ $t('disconnect') }}</a>
                        </a-popconfirm>
                    </span>
                </div>
            </template>
        </a-table>
    </div>
</template>

<script>
import formatCoords from 'formatcoords';
import CustomInput from '../../../common/custom-input';
import UiMixin from '../template/ui-mixin';

export default {
    name: 'content-online-user-info-pane',
    components: {
        CustomInput
    },
    mixins: [UiMixin],
    data() {
        return {
            isSearchById: false,
            records: [],
            ids: '',
            loading: false,
            scrollMaxHeight: '100%',
            selectedRowKeys: [],
            selectedRows: [],
            rowSelection: {
                onChange: (selectedRowKeys) => {
                    this.updateSelectedRecordKeys(selectedRowKeys);
                }
            }
        };
    },
    computed: {
        admin() {
            return this.$store.getters.admin;
        },
        columns() {
            const columns = [{
                title: this.$t('userId'),
                dataIndex: 'userId',
                width: '20%',
                slots: {customRender: 'userId'}
            },
            {
                title: this.$t('userStatus'),
                dataIndex: 'userStatus',
                width: '15%',
                slots: {customRender: 'userStatus'}
            },
            {
                title: this.$t('onlineDevice'),
                dataIndex: 'deviceType',
                width: '15%',
                slots: {customRender: 'deviceType'}
            },
            {
                title: this.$t('loginDateAndOnlineTime'),
                dataIndex: 'loginDate',
                width: '20%',
                slots: {customRender: 'loginDate'}
            },
            {
                title: this.$t('currentLocation'),
                dataIndex: 'location',
                width: '20%',
                slots: {customRender: 'location'}
            },
            {
                title: this.$t('operation'),
                dataIndex: 'operation',
                width: '10%',
                slots: {customRender: 'operation'}
            }];
            columns.forEach(column => {
                if (column.key !== 'operation' && !['TREE'].includes(String(column.type).toUpperCase())) {
                    column.sorter = (a, b) => this.$util.sort(a[column.key], b[column.key]);
                }
                return columns;
            });
            return columns;
        }
    },
    watch: {
        records() {
            this.updateSelectedRecordKeys();
        },
        '$store.getters.tab'(val) {
            if (this.myTab === val) {
                setTimeout(() => this.$refs.table?.refreshTableUi());
            }
        }
    },
    mounted() {
        this.myTab = this.$store.getters.tab;
        if (!this.myTab) {
            throw new Error('Failed to get the tab of the current page');
        }
        window.addEventListener('resize', this.refreshTableUi);
    },
    unmounted() {
        window.removeEventListener('resize', this.refreshTableUi);
    },
    methods: {
        search() {
            this.loading = true;
            const userIds = this.ids
                .split(',')
                .filter(value => !isNaN(parseInt(value)))
                .map(value => parseInt(value));
            this.$http.get(this.$rs.apis.userStatus, {
                params: {
                    ids: userIds?.join(',')
                }
            })
                .then(response => {
                    if (response.status === 204) {
                        this.records = [];
                        return;
                    }
                    const records = response.data.data.flatMap(record => {
                        const array = [];
                        record.key = record.userId;
                        if (record.sessionMap) {
                            for (const key of Object.keys(record.sessionMap)) {
                                const target = this.$util.copy(record);
                                const source = target.sessionMap[key];
                                const friendlyDate = this.$moment(source.loginDate).fromNow();
                                source.loginDate = `${source.loginDate} (${friendlyDate})`;
                                if (source.location?.longitude && source.location?.latitude) {
                                    source.location = formatCoords(source.location.latitude, source.location.longitude).format();
                                }
                                delete target.sessionMap;
                                array.push(Object.assign(target, source));
                            }
                        } else {
                            array.push(record);
                        }
                        return array;
                    });
                    this.records = userIds.map(userId => (records.find(record => record.key === userId) || {
                        key: userId,
                        userId,
                        userStatus: 'NONEXISTENT'
                    }));
                })
                .catch(error => {
                    if (error.response?.status === 404) {
                        this.records = [];
                    }
                    this.$error(this.$t('updateFailed'), error);
                })
                .finally(() => {
                    this.loading = false;
                });
        },
        setSelectedDevicesOffline() {
            if (!this.selectedRows.length) {
                this.$message.error(this.$t('noRecordsSelected'));
                return;
            }
            if (!this.selectedRows.some(value => !['OFFLINE', 'NONEXISTENT'].includes(value.userStatus))) {
                this.$message.success(this.$t('disconnectSuccessfully'));
                return;
            }
            const userIds = this.selectedRowKeys.filter((v, i, a) => a.indexOf(v) === i);
            const deviceTypes = this.selectedRows
                .filter(record => record.deviceType)
                .map(record => record.deviceType);
            this.disconnectUsers(userIds, deviceTypes);
        },
        disconnectUsers(ids, deviceTypes) {
            if (!deviceTypes.length) {
                console.error('The device types to disconnect must not be empty');
                return;
            }
            this.loading = true;
            const params = {
                ids: ids.join(','),
                deviceTypes: deviceTypes ? deviceTypes.join(',') : undefined
            };
            this.$http.put(`${this.$rs.apis.userStatus}?${this.$qs.encode(params)}`, {
                onlineStatus: 'OFFLINE'
            })
                .then(() => {
                    this.$message.success(this.$t('disconnectSuccessfully'));
                    this.records = this.records.map(record => {
                        if (!ids.includes(record.id)) {
                            record.userStatus = 'OFFLINE';
                        }
                    });
                })
                .catch(error => {
                    this.$error(this.$t('failedToDisconnect'), error);
                })
                .finally(() => this.loading = false);
        },
        updateSelectedRecordKeys(keys) {
            if (!keys) {
                keys = this.selectedRowKeys
                    .filter(key => this.records.some(record => record.rowKey === key));
            }
            this.selectedRowKeys = keys;
            this.selectedRows = this.selectedRows
                .filter(row => this.records.some(record => record.rowKey === row.rowKey));
            this.rowSelection.selectedRowKeys = keys;
        }
    }
};
</script>
<style scoped>
.content-online-user {
    display: flex;
    flex-direction: column;
}

.search-filter-id {
    max-width: 200px;
}

.search-button {
    margin-bottom: 18px;
}
</style>