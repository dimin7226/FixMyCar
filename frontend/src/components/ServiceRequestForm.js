import React, { useState, useEffect } from 'react';
import { Form, Input, Button, message, Card, Space, Select } from 'antd';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';

const { Option } = Select;

const ServiceRequestForm = () => {
  const [form] = Form.useForm();
  const navigate = useNavigate();
  const { id } = useParams();
  const [loading, setLoading] = useState(false);
  const [initialValues, setInitialValues] = useState(null);
  const [cars, setCars] = useState([]);
  const [serviceCenters, setServiceCenters] = useState([]);

  useEffect(() => {
    fetchCars();
    fetchServiceCenters();
    if (id) {
      fetchRequest();
    }
  }, [id]);

  const fetchCars = async () => {
    try {
      const response = await axios.get('http://localhost:8080/home/cars');
      setCars(response.data);
    } catch (error) {
      message.error('Ошибка при загрузке списка автомобилей');
    }
  };

  const fetchServiceCenters = async () => {
    try {
      const response = await axios.get('http://localhost:8080/home/service-centers');
      setServiceCenters(response.data);
    } catch (error) {
      message.error('Ошибка при загрузке списка сервисных центров');
    }
  };

  const fetchRequest = async () => {
    try {
      const response = await axios.get(`http://localhost:8080/home/requests/${id}`);
      setInitialValues(response.data);
      form.setFieldsValue({
        ...response.data,
        carId: response.data.car?.id,
        serviceCenterId: response.data.serviceCenter?.id,
      });
    } catch (error) {
      message.error('Ошибка при загрузке данных заявки');
    }
  };

  const onFinish = async (values) => {
    setLoading(true);
    try {
      const selectedCar = cars.find(car => car.id === values.carId);
      if (!selectedCar) {
        message.error('Автомобиль не найден');
        return;
      }

      // Для POST - используем query-параметры
      if (id) {
        // PUT - отправляем в body
        await axios.put(`http://localhost:8080/home/requests/${id}`, {
          customerId: selectedCar.customer.id,
          carId: values.carId,
          serviceCenterId: values.serviceCenterId,
          description: values.description
        });
      } else {
        // POST - отправляем как query-параметры
        const params = new URLSearchParams();
        params.append('customerId', selectedCar.customer.id);
        params.append('carId', values.carId);
        params.append('serviceCenterId', values.serviceCenterId);
        params.append('description', values.description);

        await axios.post('http://localhost:8080/home/requests', null, {
          params: params
        });
      }

      message.success(`Заявка успешно ${id ? 'обновлена' : 'создана'}`);
      navigate('/requests');
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
        title={id ? 'Редактировать заявку' : 'Создать заявку'}
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
          initialValues={initialValues || {}}
        >
          <Form.Item
            name="carId"
            label="Автомобиль"
            rules={[{ required: true, message: 'Пожалуйста, выберите автомобиль' }]}
          >
            <Select placeholder="Выберите автомобиль">
              {cars.map(car => (
                <Option key={car.id} value={car.id}>
                  {`${car.brand} ${car.model} (${car.year})`}
                </Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="serviceCenterId"
            label="Сервисный центр"
            rules={[{ required: true, message: 'Пожалуйста, выберите сервисный центр' }]}
          >
            <Select placeholder="Выберите сервисный центр">
              {serviceCenters.map(center => (
                <Option key={center.id} value={center.id}>
                  {center.name}
                </Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="description"
            label="Описание проблемы"
            rules={[{ required: true, message: 'Пожалуйста, опишите проблему' }]}
          >
            <Input.TextArea rows={4} />
          </Form.Item>

          <Form.Item>
            <Space>
              <Button
                type="primary"
                htmlType="submit"
                loading={loading}
                style={{ width: 120 }}
              >
                {id ? 'Сохранить' : 'Создать'}
              </Button>
              <Button
                onClick={() => navigate('/requests')}
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

export default ServiceRequestForm; 