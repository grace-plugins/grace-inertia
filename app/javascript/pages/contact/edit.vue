<script>
import { Form, Link } from '@inertiajs/vue3'

export default {
  components: {
    Form,
    Link
  },
  props: {
    data: Object,
    errors: Object
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
                  <Link class="nav-link" href="/contact/index">
                      <i class="bi bi-journals"></i>Contact List
                  </Link>
              </li>
              <li class="nav-item">
                  <Link class="nav-link" href="/contact/create">
                      <i class="bi bi-journal-plus"></i>Create Contact
                  </Link>
              </li>
              <li class="nav-item">
                  <Link class="nav-link" :href="`/contact/show/${data.id}`">
                      <i class="bi bi-journal-text"></i>Show Contact
                  </Link>
              </li>
          </ul>
      </div>
    </section>
    <section>
      <Form :action="`/contact/update/${data.id}`" method="put" :class="{ 'needs-validation': errors }">
        <ol class="property-list">
          <li class="form-group row mb-2">
            <label for="firstName" class="property-label col-3 col-form-label text-end">First Name</label>
            <div class="col-sm-9">
              <input id="firstName" type="text" class="form-control" :class="{ 'is-invalid': errors.firstName }" name="firstName" :value="data.firstName">
              <div class="invalid-feedback" v-if="errors.firstName">{{ errors.firstName }}</div>
            </div>
          </li>
          <li class="form-group row mb-2">
            <label for="lastName" class="property-label col-3 col-form-label text-end">Last Name</label>
            <div class="col-sm-9">
              <input id="lastName" type="text" class="form-control" :class="{ 'is-invalid': errors.lastName }" name="lastName" :value="data.lastName">
              <div class="invalid-feedback" v-if="errors.lastName">{{ errors.lastName }}</div>
            </div>
          </li>
          <li class="form-group row">
            <label for="email" class="property-label col-3 col-form-label text-end">Last Name</label>
            <div class="col-sm-9">
              <input id="email" type="text" class="form-control" :class="{ 'is-invalid': errors.email }" name="email" :value="data.email">
              <div class="invalid-feedback" v-if="errors.email">{{ errors.email }}</div>
            </div>
          </li>
        </ol>
        <fieldset class="buttons offset-3 d-grid gap-2 d-md-flex">
          <button type="submit" class="btn btn-primary">
            <i class="bi bi-journal-check"></i>Update
          </button>
        </fieldset>
      </Form>
    </section>
  </div>
</div>
</template>