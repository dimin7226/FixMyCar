import React from 'react';
import './ServiceCard.css';
import { Rate, Button } from 'antd';

const ServiceCard = ({ service }) => {
    return (
        <div className="service-card">
            <h3>{service.name}</h3>
            <Rate disabled defaultValue={service.rating} />
            <p>{service.address}</p>
            <p>{service.phone}</p>
            <Button type="primary">Записаться</Button>
        </div>
    );
};

export default ServiceCard;