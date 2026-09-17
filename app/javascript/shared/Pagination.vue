<script>
import { Link } from '@inertiajs/vue3'

export default {
  components: {
    Link
  },
  props: {
    pagination: Object
  }
}
</script>

<template>
  <nav aria-label="Page navigation">
    <ul class="pagination">
      <li class="page-item" :class="{ 'disabled': !pagination.hasPrevious }">
        <Link class="page-link" :href="pagination.previousPageUrl">Previous</Link>
      </li>
      <template v-for="link in pagination.links">
        <li class="page-item" :class="{ 'active': link.active }">
          <Link class="page-link" :href="link.url">{{ link.label }}</Link>
        </li>
      </template>
      <li class="page-item" :class="{ 'disabled': !pagination.hasNext }">
        <Link class="page-link" :href="pagination.nextPageUrl">Next</Link>
      </li>
    </ul>
  </nav>
</template>
