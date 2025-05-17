import React from 'react';
import './ServiceList.css';
import ServiceCard from '../ServiceCard/ServiceCard';
import FilterPanel from '../FilterPanel/FilterPanel';

const ServiceList = ({ services }) => {
    return (
        <div className="service-list">
            <FilterPanel />
            <div className="services-grid">
                {services.map(service => (
                    <ServiceCard key={service.id} service={service} />
                ))}
            </div>
        </div>
    );
};

export default ServiceList;