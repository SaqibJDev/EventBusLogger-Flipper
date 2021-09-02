import React from 'react';
import { Button, Typography } from 'antd';
import {DeleteOutlined} from '@ant-design/icons';
import {  Layout,
  PluginClient,
  usePlugin,
  createState,
  createDataSource,
  DataTableColumn,
  DataTable,
  useValue,
  DataInspector,
  Panel,
  DetailSidebar} from 'flipper-plugin';
  import { KeyValueTable, KeyValueItem } from './KeyValueTable'

type Event = {
  id: string,
  name: string,
  eventType: string,
  eventBody: string,
  timestamp: string,
  stackTrace: string,
  keyValueMap?: Object
};

type Events = {
  newEvent: Event;
};

const baseColumns: DataTableColumn<Event>[] = [
  {
    key: 'name',
    title: 'Name',
    width: 350
  },
  {
    key: 'eventType',
    title: 'Event Type',
    width: 120
  },
  {
    key: 'timestamp',
    title: 'Timestamp',
    width: 250
  }
];

export function plugin(client: PluginClient<Events, {}>) {
  const selectedID = createState<string | null>(null, {persist: 'selection'});
  const columns = createState<DataTableColumn<Event>[]>(baseColumns); // not persistable
  const events = createDataSource<Event>([], {
    key: 'id',
  });

  client.onMessage('newEvent', (event) => {
    events.append(event);
  });

  function onSelect(event: Event) {
    selectedID.set(event.id);
  }

  function clearLogs() {
    events.clear();
  }

  return {
    columns,
    events,
    selectedID,
    onSelect,
    clearLogs
  };
}

export function Component() {
  const instance = usePlugin(plugin);
  const columns = useValue(instance.columns);
  return (
    <>
    <Layout.Container
         grow
         key={
           columns.length /* make sure to reset the table if columns change */
         }>
         <DataTable
           columns={columns}
           dataSource={instance.events}
           onSelect={instance.onSelect}
           enableAutoScroll
           extraActions={
             <Layout.Horizontal gap>
               <Button title="Clear logs" onClick={instance.clearLogs}>
                 <DeleteOutlined />
               </Button>
             </Layout.Horizontal>
           }
         />
         <DetailSidebar width={550}>
           { <Sidebar /> }
         </DetailSidebar>
       </Layout.Container>
       </>
  );
}

function Sidebar() {
  const instance = usePlugin(plugin);
  const selectedId = useValue(instance.selectedID);
  const selectedEvent = instance.events.getById(selectedId!);

  if (!selectedEvent) {
    return (
      <Layout.Container pad grow center>
        <Typography.Text type="secondary">No event selected</Typography.Text>
      </Layout.Container>
    );
  }
  return renderSidebar(selectedEvent)
}

function renderSidebar(event: Event) {
  var eventBody = ""
  try {
    eventBody = JSON.parse(event.eventBody)
  } catch {
    eventBody = ""
  }
  
  return (
    <>
    <Panel key="className" title={getClassName(event.name)}>
      { event.keyValueMap && <KeyValueTable items={getKeyValueItems(event.keyValueMap)} />  }
    </Panel>
    <Panel key="classBody" title="Event Body">
      <DataInspector data={eventBody} />
    </Panel>
    <Panel key="stackTrace" title={'Stack Trace'}>
      <DataInspector data={event.stackTrace} />
    </Panel>
  </>
  );
}

function getKeyValueItems(keyValueMap?: Object): KeyValueItem[] {
  const items: KeyValueItem[] = [];
  if(!keyValueMap) return items
  for (let [key, value] of Object.entries(keyValueMap)) {
    items.push({
        key,
        value,
      });
  }
  return items
}

function getClassName(str: string) {
  var className = str.split('(')
  if(className.length > 1){
    return className[0]
  } else {
    var className = str.split('.')
    return className[className.length-1].split('@')[0]
  }
}