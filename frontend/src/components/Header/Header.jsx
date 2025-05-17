import React from 'react';
import './Header.css';
import { Input, Button } from 'antd';

const Header = () => {
    return (
        <header className="header">
            <div className="logo">FixMyCar</div>
            <Input.Search placeholder="Найти сервис..." className="search-bar" />
            <div className="auth-buttons">
                <Button type="text">Войти</Button>
                <Button type="primary">Записаться</Button>
            </div>
        </header>
    );
};

export default Header;