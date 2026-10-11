
export interface Transaction {
    id: number;
    accountId: number;
    type: string;
    date: number;
    amount: number;
    description: string;
}
