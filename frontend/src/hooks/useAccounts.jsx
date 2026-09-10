import { useEffect, useState } from "react";
import { accountAPI } from "../api/accountAPI";

function useAccounts() {
    const [accounts, setAccounts] = useState([]);
    const [selectedAccount, setSelectedAccount] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    async function loadAccounts() {
        try {
            setLoading(true);
            setError("");
            const response = await accountAPI.getMyAccounts();
            const data = response.data;
            const accountList = Array.isArray(data) ? data : data?.accounts ?? [];
            setAccounts(accountList);
            const primaryAccount = accountList.find((account) => account.isPrimary === true) ?? accountList[0] ?? null;
            setSelectedAccount(primaryAccount);
        }catch(err) {
            setError(err.response?.data?.message);
        }finally {
            setLoading(false);
        }
    }
    async function createAccount() {
        try {
            setError("");
            await accountAPI.createAccount();
            await loadAccounts();
        }catch(err) {
            setError(err.response?.data?.message);
        }
    }
    async function setPrimaryAccount(accountNumber) {
        try {
            setError("");
            await accountAPI.setPrimaryAccount(accountNumber);
            await loadAccounts();
        }catch(err) {
            setError(err.response?.data?.message);
            throw err;
        }
    }
    useEffect(() => {
        loadAccounts();
    }, []);
    return {
        accounts,
        selectedAccount,
        createAccount,
        setPrimaryAccount,
        loading,
        error,
    };
}
export {useAccounts}