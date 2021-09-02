import {TestUtils} from 'flipper-plugin';
import * as EventBusLogger from '..';

test('It can store events', () => {
  const {instance, sendEvent} = TestUtils.startPlugin(EventBusLogger);

  expect(instance.events).toEqual({});
  expect(instance.selectedID.get()).toBeNull();

  sendEvent('newEvent', {
    id: "1",
    name: "MyStickyEvent",
    eventType: "postSticky",
    timestamp: '2021-08-20 00:21:00.Zz',
    stackTrace: "Executed at Line 17",
    eventBody: "{}"
  });
  sendEvent('newEvent', {
    id: "2",
    name: "MyNormalEvent",
    eventType: "postEvent",
    timestamp: '2021-08-20 00:21:00.Zz',
    stackTrace: "Executed at Line 1",
    eventBody: "{}"
  });

  expect(instance.events).toMatchInlineSnapshot(`
    Object {
      "1": Object {
        id: "1",
        name: "MyStickyEvent",
        eventType: "postSticky",
        timestamp: '2021-08-20 00:21:00.Zz',
        stackTrace: "Executed at Line 1",
        eventBody: "{}"
      },
      "2": Object {
        id: "2",
        name: "MyNormalEvent",
        eventType: "postEvent",
        timestamp: '2021-08-20 00:21:00.Zz',
        stackTrace: "Executed at Line 1",
        eventBody: "{}"
      },
    }
  `);
});