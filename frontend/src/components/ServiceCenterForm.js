import React, { useState, useEffect } from 'react';
import { Form, Input, Button, message, Card, Space } from 'antd';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';

const ServiceCenterForm = () => {
  const [form] = Form.useForm();
  const navigate = useNavigate();
  const { id } = useParams();
  const [loading, setLoading] = useState(false);
  const [initialValues, setInitialValues] = useState(null);

  useEffect(() => {
    if (id) {
      fetchServiceCenter();
    }
  }, [id]);

  const fetchServiceCenter = async () => {
    try {
      const response = await axios.get(`http://localhost:8080/home/service-centers/${id}`);
      const data = response.data;

      // Преобразуем телефон в нужный формат, если он пришел без форматирования
      const formattedPhone = data.phone.includes('(')
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
      message.error('Ошибка при загрузке данных сервисного центра');
      console.error('Ошибка:', error);
    }
  };

  const formatPhoneNumber = (value) => {
    if (!value) return '+375 (';

    // Удаляем все нецифровые символы, кроме плюса
    let cleaned = value.replace(/[^\d+]/g, '');

    // Если номер не начинается с +375, добавляем код страны
    if (!cleaned.startsWith('+375')) {
      cleaned = '+375' + cleaned.replace(/^\+/, '');
    }

    // Ограничиваем длину (код страны + 9 цифр)
    cleaned = cleaned.substring(0, 13);

    // Форматируем по шаблону +375 (__) ___ __ __
    const parts = cleaned.match(/^(\+\d{3})(\d{2})(\d{3})(\d{2})(\d{2})$/);
    if (parts) {
      return `${parts[1]} (${parts[2]}) ${parts[3]}-${parts[4]}-${parts[5]}`;
    }

    // Частичное форматирование при вводе
    if (cleaned.length > 4) {
      return cleaned.replace(/^(\+\d{3})(\d{0,2})(\d{0,3})(\d{0,2})(\d{0,2})/,
          (_, p1, p2, p3, p4, p5) => {
            let result = p1;
            if (p2) result += ` (${p2}`;
            if (p3) result += `) ${p3}`;
            if (p4) result += `-${p4}`;
            if (p5) result += `-${p5}`;
            return result;
          });
    }

    return cleaned;
  };

  const handlePhoneChange = (e) => {
    const { value, selectionStart } = e.target;
    const formattedValue = formatPhoneNumber(value);

    form.setFieldsValue({ phone: formattedValue });

    // Корректируем позицию курсора
    setTimeout(() => {
      const diff = formattedValue.length - value.length;
      const newPos = Math.max(selectionStart + diff, 6); // Минимум после "+375 ("
      e.target.setSelectionRange(newPos, newPos);
    }, 0);
  };

  const onFinish = async (values) => {
    setLoading(true);
    try {
      // Сохраняем номер в отформатированном виде
      const dataToSend = {
        ...values,
        phone: values.phone // Сохраняем как есть (+375 (29) 123-45-67)
      };

      if (id) {
        await axios.put(`http://localhost:8080/home/service-centers/${id}`, dataToSend);
        message.success('Сервисный центр успешно обновлен');
      } else {
        await axios.post('http://localhost:8080/home/service-centers', dataToSend);
        message.success('Сервисный центр успешно добавлен');
      }
      navigate('/service-centers');
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
            title={id ? 'Редактировать сервисный центр' : 'Добавить сервисный центр'}
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
                name="name"
                label="Название"
                rules={[{ required: true, message: 'Пожалуйста, введите название' }]}
            >
              <Input />
            </Form.Item>

            <Form.Item
                name="address"
                label="Адрес"
                rules={[{ required: true, message: 'Пожалуйста, введите адрес' }]}
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
                    message: 'Введите корректный номер (+375 (__) ___ __ __)'
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
                    onClick={() => navigate('/service-centers')}
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

export default ServiceCenterForm;