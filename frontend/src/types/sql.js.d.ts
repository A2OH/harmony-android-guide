declare module 'sql.js' {
  interface SqlJsStatic {
    Database: new (data?: ArrayLike<number>) => Database;
  }
  interface Database {
    run(sql: string, params?: any[]): Database;
    exec(sql: string): { columns: string[]; values: any[][] }[];
    prepare(sql: string): Statement;
    close(): void;
  }
  interface Statement {
    bind(params?: any[]): boolean;
    step(): boolean;
    getAsObject(): any;
    free(): void;
  }
  interface SqlJsConfig {
    locateFile?: (file: string) => string;
  }
  export default function initSqlJs(config?: SqlJsConfig): Promise<SqlJsStatic>;
  export type { Database, Statement, SqlJsStatic };
}
