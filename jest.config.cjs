module.exports = {
    testEnvironment: 'jest-environment-jsdom',
    transform: {
        '^.+\\.jsx?$': 'babel-jest',
    },
    moduleFileExtensions: ['js', 'jsx', 'ts', 'tsx', 'json', 'node'],
    setupFilesAfterEnv: ['<rootDir>/setupTests.js'],
    moduleNameMapper: {
        '\\.(css|less|sass|scss)$': 'identity-obj-proxy', // Mock CSS imports
        '\\.(jpg|jpeg|png|gif|svg)$': '<rootDir>/mocks/fileMock.js', // Mock image imports
    },
};
