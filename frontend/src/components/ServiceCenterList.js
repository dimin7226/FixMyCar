import React, { useState, useEffect } from 'react';
import { Table, Button, Space, message, Card, Tag } from 'antd';
import { EditOutlined, DeleteOutlined } from '@ant-design/icons';
import axios from 'axios';

const ServiceCenterList = () => {
  const [serviceCenters, setServiceCenters] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchServiceCenters();
  }, []);

  const fetchServiceCenters = async () => {
    setLoading(true);
    try {
      const response = await axios.get('http://localhost:8080/home/service-centers');
      setServiceCenters(response.data);
    } catch (error) {
      message.error('Ошибка при загрузке списка сервисных центров');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    try {
      await axios.delete(`http://localhost:8080/home/service-centers/${id}`);
      message.success('Сервисный центр успешно удален');
      fetchServiceCenters();
    } catch (error) {
      message.error('Ошибка при удалении сервисного центра');
    }
  };

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
      render: (text) => <Tag color="geekblue">{text}</Tag>,
    },
    {
      title: 'Название',
      dataIndex: 'name',
      key: 'name',
      width: 200,
      render: (text) => <span className="text-bold">{text}</span>,
    },
    {
      title: 'Адрес',
      dataIndex: 'address',
      key: 'address',
      width: 300,
    },
    {
      title: 'Телефон',
      dataIndex: 'phone',
      key: 'phone',
      width: 150,
      render: (text) => <a href={`tel:${text}`}>{text}</a>,
    },
    {
      title: 'Действия',
      key: 'actions',
      width: 120,
      fixed: 'right',
      render: (_, record) => (
          <Space size="small">
            <Button
                type="text"
                icon={<EditOutlined style={{ color: '#1890ff' }} />}
                href={`/service-centers/edit/${record.id}`}
            />
            <Button
                type="text"
                icon={<DeleteOutlined style={{ color: '#ff4d4f' }} />}
                onClick={() => handleDelete(record.id)}
            />
          </Space>
      ),
    },
  ];

  return (
      <div style={{ padding: '24px' }}>
        <Card
            title={<span style={{ fontSize: '18px', fontWeight: '500' }}>Список сервисных центров</span>}
            bordered={false}
            extra={
              <Button type="primary" href="/service-centers/add" size="middle">
                Добавить сервисный центр
              </Button>
            }
            style={{ boxShadow: '0 2px 8px rgba(0, 0, 0, 0.09)' }}
        >
          <Table
              columns={columns}
              dataSource={serviceCenters}
              rowKey="id"
              loading={loading}
              pagination={{
                pageSize: 10,
                showSizeChanger: false,
                showTotal: (total) => `Всего ${total} сервисных центров`
              }}
              scroll={{ x: 900 }}
              style={{ marginTop: '16px' }}
              rowClassName={() => 'table-row'}
          />
        </Card>

        <style jsx global>{`
        .text-bold {
          font-weight: 500;
        }
        .ant-table .table-row:hover td {
          background: #fafafa !important;
        }
        .ant-table-thead > tr > th {
          background: #f8f8f8;
          font-weight: 500;
        }
        .ant-card-head-title {
          padding: 16px 0;
        }
      `}</style>
      </div>
  );
};

export default ServiceCenterList;