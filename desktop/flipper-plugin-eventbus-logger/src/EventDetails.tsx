import React from 'react';
import {Component} from 'react';
import querystring from 'querystring';

import {
  DataInspector,
  Layout,
  Panel,
  styled,
  theme,
  CodeBlock,
} from 'flipper-plugin';
import {Select, Typography} from 'antd';

type EventDetailsProps = {
    row: Row;
    bodyFormat: string;
    onSelectFormat: (bodyFormat: string) => void;
    onCopyText(test: string): void;
  };

export default class EventDetails extends Component<EventDetailsProps> {
    urlColumns = (url: URL) => {
      return [
        {
          key: 'Full URL',
          value: url.href,
        },
        {
          key: 'Host',
          value: url.host,
        },
        {
          key: 'Path',
          value: url.pathname,
        },
        {
          key: 'Query String',
          value: url.search,
        },
      ];
    };