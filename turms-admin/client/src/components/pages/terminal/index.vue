<template>
    <div class="client-terminal-pane">
        <client-terminal
            ref="cliTerminal"
            class="client-terminal-pane__cli-terminal"
            :title="$t('cliConsole')"
            :options="cliTerminalOptions"
            :show-setting="true"
            @settingClick="openSettingModal"
        />
        <client-terminal
            ref="notificationTerminal"
            class="client-terminal-pane__notification-terminal"
            :title="$t('notifications')"
            :options="notificationTerminalOptions"
        />
        <a-modal
            v-model:visible="modalVisible"
            :title="$t('settings')"
            @ok="handleOk"
        >
            <a-form
                ref="form"
                :model="formState"
            >
                <a-form-item
                    label="WebSocket URL"
                    name="url"
                    :rules="$validator.create({required: true, isWsUrl: true})"
                >
                    <a-input v-model:value="formState.url" />
                </a-form-item>
                <a-form-item
                    :label="$t('useSharedContext')"
                    name="useSharedContext"
                >
                    <a-switch v-model:checked="formState.useSharedContext" />
                </a-form-item>
                <a-form-item
                    :label="$t('commandsHistorySize')"
                    name="commandsHistorySize"
                >
                    <a-input-number
                        v-model:value="formState.commandsHistorySize"
                        :min="0"
                        :max="1000"
                    />
                </a-form-item>
            </a-form>
        </a-modal>
    </div>
</template>

<script>
import TurmsClient from 'turms-client-js';
import ClientTerminal from './client-terminal';
import Ast from '../../../assets/turms-client-ast.json';

const ONBOARD_MESSAGES = [
    `Current version of turms-client-js: ${TurmsClient.version}`,
    'Input commands, e.g. "user.login(1, \'123\')"',
    '"help" for details'
];
const HELP = `* Builtin Objects:
    * Turms Client: client
    * Services:
        * conversation
        * group
        * message
        * notification
        * storage
        * user
* Command Examples:
    * user.login(1, '123')
    * message.sendMessage(false, 1, null, 'This is my message')
`;
const CONTEXT = `
const client = this;
const conversation = client.conversationService;
const group = client.groupService;
const message = client.messageService;
const notification = client.notificationService;
const storage = client.storageService;
const user = client.userService;
return `;
const AST_ROOT = [
    {
        name: 'help'
    },
    {
        name: 'this',
        type: 'TurmsClient'
    },
    {
        name: 'client',
        type: 'TurmsClient'
    },
    {
        name: 'conversation',
        type: 'ConversationService'
    },
    {
        name: 'group',
        type: 'GroupService'
    },
    {
        name: 'message',
        type: 'MessageService'
    },
    {
        name: 'notification',
        type: 'NotificationService'
    },
    {
        name: 'storage',
        type: 'StorageService'
    },
    {
        name: 'user',
        type: 'UserService'
    }
].map(val => ({syntax: 'variable', ...val}));
const MESSAGE_FOR_VOID_FUNCTION = '(Done)';

export default {
    name: 'client-terminal-pane',
    components: {
        ClientTerminal
    },
    data() {
        const settings = {
            url: `ws://${window.location.hostname}:10510`,
            useSharedContext: false,
            commandsHistorySize: 0,
            ...localStorage.getItem(this.$rs.storage.terminalSettings) || {}
        };
        return {
            modalVisible: false,
            settings,
            formState: this.$util.copy(settings),
            cliTerminalOptions: {
                history: this.$storage.getArray(this.$rs.storage.terminalCommandHistory),
                ast: Ast,
                astRoot: AST_ROOT
            },
            notificationTerminalOptions: {
                disableStdin: true
            }
        };
    },
    mounted() {
        this.initCliTerminal();
        this.notificationTerminal = this.$refs.notificationTerminal.getTerminal();
        this.client = this.initClient();
    },
    methods: {
        openSettingModal() {
            this.formState = this.$util.copy(this.settings);
            this.modalVisible = true;
        },
        closeSettingModal() {
            this.modalVisible = false;
        },
        async handleOk() {
            let values;
            try {
                values = await this.$refs.form.validateFields();
            } catch (errorInfo) {
                return;
            }
            const isClientSettingsChanged = this.settings.useSharedContext !== values.useSharedContext
                || this.settings.url !== values.url;
            this.settings = {
                ...this.settings,
                ...values
            };
            if (isClientSettingsChanged) {
                await this.client.close();
                this.client = this.initClient();
                this.notificationTerminal.writeMsg({
                    msg: 'The previous client has been closed, and a new client with the new settings have been created'
                });
            }
            this.closeSettingModal();
        },
        async executeCmd(cmd) {
            if (cmd === 'help') {
                return {
                    type: 'info',
                    msg: HELP.replace(/\n/g, '\r\n')
                };
            }
            try {
                const func = new Function(CONTEXT + cmd);
                let result = func.call(this.client);
                let isFunction;
                if (result instanceof Promise) {
                    isFunction = true;
                    result = await result;
                } else if (cmd.endsWith(')')) {
                    isFunction = true;
                }
                result = result == null && isFunction
                    ? MESSAGE_FOR_VOID_FUNCTION
                    : this.stringify(result);
                return {
                    type: isFunction ? 'success' : 'info',
                    msg: result,
                    newLine: true
                };
            } catch (e) {
                return {
                    type: 'error',
                    msg: this.stringify(e),
                    newLine: true
                };
            }
        },
        initClient() {
            const terminal = this.notificationTerminal;
            const client = new TurmsClient({
                wsUrl: this.settings.url,
                useSharedContext: this.settings.useSharedContext
            });
            client.userService.addOnOnlineListener(() => {
                terminal.writeMsg({
                    msg: 'Go online'
                });
            });
            client.userService.addOnOfflineListener(() => {
                terminal.writeMsg({
                    msg: 'Go offline'
                });
            });
            client.messageService.addMessageListener(m => {
                terminal.writeMsg({
                    msg: 'Received message: ' + this.stringify(m)
                });
            });
            client.notificationService.addNotificationListener(n => {
                terminal.writeMsg({
                    msg: 'Received notification: ' + this.stringify(n)
                });
            });
            return client;
        },

        initCliTerminal() {
            const cliTerminal = this.$refs.cliTerminal.getTerminal();
            cliTerminal.onLine = cmd => {
                this.$storage.push(this.$rs.storage.terminalCommandHistory,
                    cmd,
                    this.settings.commandsHistorySize);
                return this.executeCmd(cmd);
            };
            for (let i = 0; i < ONBOARD_MESSAGES.length; i++) {
                cliTerminal.writeMsg({
                    type: 'info',
                    msg: ONBOARD_MESSAGES[i],
                    newLine: i === ONBOARD_MESSAGES.length - 1
                });
            }
            cliTerminal.startNewLine();
            cliTerminal.focus();
        },

        stringify(obj) {
            let msg;
            try {
                if (typeof obj === 'function') {
                    return '[Function]';
                }
                msg = obj.message || JSON.stringify(obj, null, '  ');
                return msg.replace(/\n/g, '\r\n');
            } catch (e) {
                // Suppress "TypeError: Converting circular structure to JSON"
                return obj;
            }
        }
    }
};
</script>

<style lang="scss">
.client-terminal-pane {
    display: flex;
    width: 100%;
    height: 100%;

    &__cli-terminal,
    &__notification-terminal {
        flex-grow: 1;
        max-width: 50%;
    }

    &__notification-terminal {
        margin-left: 8px;
    }
}
</style>