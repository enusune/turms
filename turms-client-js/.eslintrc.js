module.exports = {
    "root": true,
    "env": {
        "jest": true,
    },
    "parser": "@typescript-eslint/parser",
    "parserOptions": {
        "ecmaVersion": 2019,
        "sourceType": "module",
        "ecmaFeatures": {
            "jsx": false
        },
        "project": "tsconfig.json"
    },
    "extends": [
        "eslint:recommended",
        "plugin:@typescript-eslint/eslint-recommended",
        "plugin:@typescript-eslint/recommended"
    ],
    "rules": {
        "@typescript-eslint/ban-ts-ignore": "off",
        "@typescript-eslint/ban-ts-comment": "off",
        "@typescript-eslint/explicit-function-return-type": "error",
        "@typescript-eslint/no-namespace": "off",
        "@typescript-eslint/semi": ["error"],
        "no-throw-literal": "error",
        "no-trailing-spaces": "error",
        "object-curly-spacing": ["error", "always"],
        "quotes": ["error", "single"]
    },
    ignorePatterns: [".eslintrc.js"]
};
