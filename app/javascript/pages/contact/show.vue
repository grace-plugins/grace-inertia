<script>
import { Form, Link } from '@inertiajs/vue3'

export default {
  components: {
    Form,
    Link
  },
  remember: 'form',
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
                  <Link class="nav-link" :href="`/contact/edit/${data.id}`">
                      <i class="bi bi-journal-text"></i>Edit Contact
                  </Link>
              </li>
          </ul>
      </div>
    </section>
    <section>
      <Form :action="`/contact/delete/${data.id}`" method="delete">
        <ol class="property-list">
            <li class="form-group row mb-2">
                <label for="firstName" class="property-label col-3 col-form-label text-end">First Name</label>
                <div class="col-sm-9">
                    <input type="text" readonly class="form-control-plaintext" id="firstName" name="firstName" :value="data.firstName">
                </div>
            </li>
            <li class="form-group row mb-2">
                <label for="lastName" class="property-label col-3 col-form-label text-end">Last Name</label>
                <div class="col-sm-9">
                    <input type="text" readonly class="form-control-plaintext" id="lastName" name="lastName" :value="data.lastName">
                </div>
            </li>
          <li class="form-group row">
            <label for="lastName" class="property-label col-3 col-form-label text-end">Email</label>
            <div class="col-sm-9">
              <input type="text" readonly class="form-control-plaintext" id="email" name="email" :value="data.email">
            </div>
          </li>
        </ol>
        <fieldset class="buttons offset-3 d-grid gap-2 d-md-flex">
            <Link class="btn btn-primary" :href="`/contact/edit/${data.id}`">
                <i class="bi bi-journal-text"></i>Edit
            </Link>
            <button type="submit" class="btn btn-danger">
                <i class="bi bi-journal-x"></i>Delete
            </button>
        </fieldset>
      </Form>
    </section>
  </div>
</div>
</template>