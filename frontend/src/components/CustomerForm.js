import React, { useState, useEffect } from 'react';
import { Form, Input, Button, message, Card, Space } from 'antd';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';

const CustomerForm = () => {
  const [form] = Form.useForm();
  const navigate = useNavigate();
  const { id } = useParams();
  const [loading, setLoading] = useState(false);
  const [initialValues, setInitialValues] = useState(null);

  useEffect(() => {
    if (id) {
      fetchCustomer();
    }
  }, [id]);

  const fetchCustomer = async () => {
    try {
      const response = await axios.get(`http://localhost:8080/home/customers/${id}`);
      const data = response.data;

      // Форматируем телефон если он не отформатирован
      const formattedPhone = data.phone?.includes('(')
          ? data.phone
          : formatPhoneNumber(data.phone || '+375 (');

      setInitialValues({
        ...data,
        phone: formattedPhone
      });

      form.setFieldsValue({
        ...data,
        phone: formattedPhone
      });
    } catch (error) {
      message.error('Ошибка при загрузке данных клиента');
      console.error('Ошибка:', error);
    }
  };

  const formatPhoneNumber = (value) => {
    if (!value) return '+375 (';

    // Удаляем все нецифровые символы кроме плюса
    const cleaned = value.replace(/[^\d+]/g, '');

    // Если номер не начинается с +375, добавляем код страны
    const withCountryCode = cleaned.startsWith('+375') ? cleaned : `+375${cleaned.replace('+', '')}`;

    // Ограничиваем длину (код страны + 9 цифр)
    const limited = withCountryCode.substring(0, 13);

    // Форматируем по шаблону +375 (__) ___ __ __
    const parts = limited.match(/^(\+\d{3})(\d{2})(\d{3})(\d{2})(\d{0,2})$/);
    if (parts) {
      return `${parts[1]} (${parts[2]}) ${parts[3]}-${parts[4]}${parts[5] ? `-${parts[5]}` : ''}`;
    }

    // Частичное форматирование при вводе
    if (limited.length > 4) {
      const country = limited.substring(0, 4); // +375
      const operator = limited.substring(4, 6); // 29
      const rest = limited.substring(6); // остальные цифры

      let formatted = `${country} (${operator}`;
      if (rest.length > 0) formatted += `) ${rest.substring(0, 3)}`;
      if (rest.length > 3) formatted += `-${rest.substring(3, 5)}`;
      if (rest.length > 5) formatted += `-${rest.substring(5, 7)}`;

      return formatted;
    }

    return limited;
  };

  const handlePhoneChange = (e) => {
    const { value, selectionStart } = e.target;
    const formattedValue = formatPhoneNumber(value);

    form.setFieldsValue({ phone: formattedValue });

    // Корректируем позицию курсора
    setTimeout(() => {
      const input = e.target;
      const newCursorPos = calculateNewCursorPosition(value, formattedValue, selectionStart);
      input.setSelectionRange(newCursorPos, newCursorPos);
    }, 0);
  };

  const calculateNewCursorPosition = (oldValue, newValue, oldPos) => {
    // Логика для сохранения позиции курсора при форматировании
    if (oldPos <= 6) return oldPos; // До кода оператора
    if (oldPos <= 10) return oldPos + 2; // После кода оператора
    if (oldPos <= 14) return oldPos + 3; // После первых 3 цифр
    return newValue.length; // В конец
  };

  const onFinish = async (values) => {
    setLoading(true);
    try {
      // Сохраняем номер в отформатированном виде
      const data = {
        ...values,
        phone: values.phone // сохраняем как есть (+375 (29) 123-45-67)
      };

      if (id) {
        await axios.put(`http://localhost:8080/home/customers/${id}`, data);
        message.success('Клиент успешно обновлен');
      } else {
        await axios.post('http://localhost:8080/home/customers', data);
        message.success('Клиент успешно добавлен');
      }
      navigate('/customers');
    } catch (error) {
      message.error('Ошибка при сохранении данных');
      console.error('Ошибка:', error.response?.data || error.message);
    } finally {
      setLoading(false);
    }
  };

  return (
      <div style={{ padding: '24px 0' }}>
        <Card
            title={id ? 'Редактировать клиента' : 'Добавить клиента'}
            bordered={false}
            style={{
              maxWidth: 600,
              margin: '0 auto',
              boxShadow: '0 1px 2px rgba(0, 0, 0, 0.1)'
            }}
        >
          <Form
              form={form}
              layout="vertical"
              onFinish={onFinish}
              initialValues={initialValues || { phone: '+375 (' }}
          >
            <Form.Item
                name="firstName"
                label="Имя"
                rules={[{ required: true, message: 'Пожалуйста, введите имя' }]}
            >
              <Input />
            </Form.Item>

            <Form.Item
                name="lastName"
                label="Фамилия"
                rules={[{ required: true, message: 'Пожалуйста, введите фамилию' }]}
            >
              <Input />
            </Form.Item>

            <Form.Item
                name="email"
                label="Email"
                rules={[
                  { required: true, message: 'Пожалуйста, введите email' },
                  { type: 'email', message: 'Введите корректный email' }
                ]}
            >
              <Input />
            </Form.Item>

            <Form.Item
                name="phone"
                label="Телефон"
                rules={[
                  { required: true, message: 'Пожалуйста, введите телефон' },
                  {
                    pattern: /^\+375\s\(\d{2}\)\s\d{3}-\d{2}-\d{2}$/,
                    message: 'Введите корректный номер телефона (+375 (__) ___ __ __)'
                  }
                ]}
            >
              <Input
                  placeholder="+375 (__) ___ __ __"
                  onChange={handlePhoneChange}
                  maxLength={19}
              />
            </Form.Item>

            <Form.Item>
              <Space>
                <Button
                    type="primary"
                    htmlType="submit"
                    loading={loading}
                    style={{ width: 120 }}
                >
                  {id ? 'Сохранить' : 'Добавить'}
                </Button>
                <Button
                    onClick={() => navigate('/customers')}
                    style={{ width: 120 }}
                >
                  Отмена
                </Button>
              </Space>
            </Form.Item>
          </Form>
        </Card>
      </div>
  );
};

export default CustomerForm;