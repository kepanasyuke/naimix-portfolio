export default [
  {
    files: ["assets/**/*.js"],
    languageOptions: {
      ecmaVersion: 2023,
      sourceType: "module",
      globals: {
        window: "readonly",
        document: "readonly",
        fetch: "readonly",
        Promise: "readonly",
        Object: "readonly",
        Math: "readonly",
        Number: "readonly",
        JSON: "readonly",
        Error: "readonly"
      }
    },
    rules: {
      "no-var": "error",
      "prefer-const": "error",
      eqeqeq: ["error", "always"],
      curly: ["error", "all"],
      "no-plusplus": "error",
      "no-labels": "error",
      "consistent-return": "error",
      "max-statements": ["error", 2],
      "max-lines-per-function": ["error", 20],
      complexity: ["error", 3],
      "max-depth": ["error", 2],
      "no-restricted-syntax": [
        "error",
        {
          selector: "WhileStatement",
          message: "Циклы while запрещены правилами проекта."
        },
        {
          selector: "DoWhileStatement",
          message: "Циклы do-while запрещены правилами проекта."
        },
        {
          selector: "BreakStatement",
          message: "break запрещён правилами проекта."
        },
        {
          selector: "ContinueStatement",
          message: "continue запрещён правилами проекта."
        }
      ]
    }
  }
];
