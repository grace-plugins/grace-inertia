import { createApp, h } from 'vue'
import { createInertiaApp } from '@inertiajs/vue3'

createInertiaApp({
  id: 'inertia-app',
  defaults: {
    future: {
        useScriptElementForInitialPage: false,
    },
  },
  resolve: async (name) => {
    const pages = import.meta.glob('./pages/**/*.vue')
    return (await pages[`./pages/${name}.vue`]()).default
  },
  setup ({el, App, props, plugin}) {
    createApp({ render: () => h(App, props) })
      .use(plugin)
      .mount(el)
  }
})