import React from 'react';
import './Modal.css';
import { Form, Input, DatePicker, Button } from 'antd';

const Modal = ({ visible, onClose }) => {
    return (
        <div className={`modal ${visible ? 'visible' : ''}`}>
            <Form>
                <Form.Item label="Имя">
                    <Input />
                </Form.Item>
                <Form.Item label="Телефон">
                    <Input />
                </Form.Item>
                <Form.Item label="Дата и время">
                    <DatePicker showTime />
                </Form.Item>
                <Button type="primary">Отправить</Button>
                <Button onClick={onClose}>Отмена</Button>
            </Form>
        </div>
    );
};

export default Modal;