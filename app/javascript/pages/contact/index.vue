<script>
import { Link } from '@inertiajs/vue3'
import Pagination from "../../shared/Pagination.vue";

export default {
  components: {
    Pagination,
    Link
  },
  props: {
    data: Array,
    pagination: Object,
    meta: Object
  }
}
</script>

<template>
<div id="content" role="main">
  <div class="container">
    <section class="row">
      <div class="col-12" role="navigation">
          <ul class="nav nav-pills">
              <li class="nav-item">
                  <Link class="nav-link" href="/">
                      <i class="bi bi-house-fill"></i>Home
                  </Link>
              </li>
              <li class="nav-item">
                  <Link class="nav-link" href="/contact/create">
                      <i class="bi bi-journal-plus"></i>New Contact
                  </Link>
              </li>
          </ul>
      </div>
    </section>
    <section class="row">
      <table class="table caption-top table-bordered table-hover">
        <caption>List of contacts {{ meta.pageNum }} / {{ meta.pageCount }} in {{ meta.total }}</caption>
        <thead>
          <tr>
            <th scope="col">First Name</th>
            <th scope="col">Last Name</th>
            <th scope="col">Email</th>
            <th scope="col">Operations</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="contact in data">
            <td>{{ contact.firstName }}</td>
            <td>{{ contact.lastName }}</td>
            <td>{{ contact.email }}</td>
            <td><Link :href="`/contact/show/${contact.id}`">Show</Link></td>
          </tr>
        </tbody>
      </table>
      <pagination :pagination="pagination" />
    </section>
  </div>
</div>
</template>