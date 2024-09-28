// for css modules imports to work in typescript
declare module '*.module.css' {
    const classes: { [key: string]: string };
    export default classes;
}