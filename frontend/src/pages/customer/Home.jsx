import { useState } from "react";

import { useAccounts } from "../../hooks/useAccounts.jsX";
import { formatMoney } from "../../utils/formatters";
import "../../styles/HomeCustomer.css";

export default function HomeCustomer() {
    const {accounts, selectedAccount, createAccount, setPrimaryAccount, loading: loadingAccounts, error: accountError} = useAccounts();
    const [activeTab, setActiveTab] = useState("overview");
    const [changingPrimary, setChangingPrimary] = useState(false);
    const [primaryError, setPrimaryError] = useState("");
    const [createError, setCreateError] = useState("");
    const handleSetPrimary = async(accountNumber) => {
        setChangingPrimary(true);
        setPrimaryError("");
        try {
            await setPrimaryAccount(accountNumber);
        }catch(err) {
            setPrimaryError(err.response?.data?.message);
        }finally {
            setChangingPrimary(false);
        }
    };
    const handleCreateAccount = async () => {
        setCreateError("");
        try {
            await createAccount();
        }catch(err) {
            setCreateError(err.response?.data?.message);
        }
    }
    return (
        <div className="bank-dashboard">
            <aside className="bank-sidebar">
                <div className="sidebar-section-title">Banking</div>
                <nav className="sidebar-menu">
                    <button className={`sidebar-menu-item ${ activeTab === "overview" ? "active" : ""}`}
                        onClick={() =>
                            setActiveTab("overview")
                        }
                    >
                        <span className="sidebar-icon">⌂</span>
                        <span> Overview </span>
                    </button>
                    <button className={`sidebar-menu-item ${ activeTab === "accounts" ? "active" : ""}`}
                        onClick={() =>
                            setActiveTab("accounts")
                        }
                    >
                        <span className="sidebar-icon"> ▣ </span>
                        <span> Accounts </span>
                    </button>
                </nav>
            </aside>
            <main className="bank-main">
                {activeTab === "overview" && (
                    <>
                        <section className="primary-account">
                            <div className="primary-account-glow" />
                            <div className="primary-account-content">
                                <div className="primary-account-top">
                                    <div className="account-heading">
                                        <div className="account-main-icon"> 🏦 </div>
                                        <div>
                                            <div className="account-title-row">
                                                <h1> Primary Account </h1>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div className="main-balance">
                                    <span> Available balance </span>
                                    <strong>
                                        {loadingAccounts? "0₫" : formatMoney(selectedAccount?.balance)}
                                    </strong>
                                </div>
                                <div className="primary-account-footer">
                                    <div className="primary-info">
                                        <span> Account number </span>
                                        <strong>
                                            ••••{" "} {String(selectedAccount?.accountNumber ?? "").slice(-4)}
                                        </strong>
                                    </div>
                                    <div className="primary-info">
                                        <span> Status </span>
                                        <strong className="active-status">
                                            {selectedAccount?.status || "—"}
                                        </strong>
                                    </div>
                                </div>
                            </div>
                        </section>
                    </>
                )}
                {activeTab === "accounts" && (
                    <section className="content-card">
                        <div className="content-card-header">
                            <div>
                                <h2> My Accounts </h2>
                                <span> Manage My bank accounts </span>
                            </div>
                            <button className="pink-action-btn small"
                                onClick={
                                    handleCreateAccount
                                }
                            >
                                + New account
                            </button>
                        </div>
                        {accountError && (
                            <div className="error-box">
                                {accountError}
                            </div>
                        )}
                        {primaryError && (
                            <div className="error-box">
                                {primaryError}
                            </div>
                        )}
                        {createError && (
                            <div className="error-box">
                                {createError}
                            </div>
                        )}
                        <div className="accounts-list">
                            {accounts.map(
                                (account) => (
                                    <div key={account.id} className={`account-item ${ account.isPrimary ? "primary": ""}`}>
                                        <div className="account-item-left">
                                            <div className="account-item-icon"> 🏦 </div>
                                            <div>
                                                <div className="account-item-name">
                                                    {account.isPrimary && (
                                                        <span className="pink-badge"> Primary </span>
                                                    )}
                                                </div>
                                                <span> {account.accountNumber} </span>
                                            </div>
                                        </div>
                                        <div className="account-item-balance">
                                            <span> Balance </span>
                                            <strong>{formatMoney( account.balance)}</strong>
                                        </div>
                                        <div className="account-item-status">
                                            <span>Status</span>
                                            <strong> {account.status} </strong>
                                        </div>
                                        <div>
                                            {account.isPrimary ? (
                                                <span className="primary-account-label"> ✓ Primary </span>
                                            ):(
                                                <button className="outline-pink-btn" disabled={changingPrimary}
                                                onClick={() =>
                                                    handleSetPrimary(account.accountNumber)
                                                }>
                                                {changingPrimary ? "Updating..." : "Set primary"}
                                                </button>
                                            )}
                                        </div>
                                    </div>
                                )
                            )}
                        </div>
                    </section>
                )}
            </main>
            <aside className="bank-right">
                <section className="right-card">
                    <div className="right-card-header no-border">
                        <h3>
                            <span className="heading-icon">◫</span>
                            Quick Stats
                        </h3>
                    </div>
                </section>
            </aside>
        </div>
    );
}