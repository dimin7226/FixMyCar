import React from 'react';
import './FilterPanel.css';
import { Select, Button } from 'antd';
import TagCloud from '../TagCloud/TagCloud'; // Добавлен импорт

const { Option } = Select;

const FilterPanel = () => {
    return (
        <div className="filter-panel">
            <Select mode="multiple" placeholder="Марка авто" className="filter-select">
                <Option value="toyota">Toyota</Option>
                <Option value="bmw">BMW</Option>
                <Option value="audi">Audi</Option>
            </Select>
            <TagCloud tags={["Диагностика", "Шиномонтаж", "Кузовной ремонт"]} />
            <Button type="primary">Применить</Button>
        </div>
    );
};

export default FilterPanel;