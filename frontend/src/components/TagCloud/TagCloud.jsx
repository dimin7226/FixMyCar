import React from 'react';
import './TagCloud.css';
import { Tag } from 'antd';

const TagCloud = ({ tags }) => {
    return (
        <div className="tag-cloud">
            {tags.map(tag => (
                <Tag closable key={tag}>{tag}</Tag>
            ))}
        </div>
    );
};

export default TagCloud;