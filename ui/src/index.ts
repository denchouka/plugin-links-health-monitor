import { definePlugin } from '@halo-dev/console-shared'
import LinksHealthMonitor from './views/LinksHealthMonitor.vue'
import { IconDashboard } from '@halo-dev/components'
import { markRaw } from 'vue'

export default definePlugin({
  components: {},
  routes: [
    {
      parentName: 'ToolsRoot',
      route: {
        path: '/linksHealth',
        name: 'LinksHealthMonitor',
        component: LinksHealthMonitor,
        meta: {
          title: 'Links Health Monitor',
          searchable: true,
          menu: {
            name: 'Links Health Monitor',
            group: "tool",
            icon: markRaw(IconDashboard),
            priority: 0,
            mobile: true
          },
        },
      },
    },
  ],
  extensionPoints: {},
})
