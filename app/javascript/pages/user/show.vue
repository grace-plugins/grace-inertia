<script>
import { Link } from '@inertiajs/vue3'

export default {
  components: {
    Link
  }
}
</script>

<template>
<div class="container">
    <section class="row">
        <h1>Show User</h1>
        <ul>
            <li><Link href="/user/create">New</Link></li>
            <li><Link href="/user/edit">Edit</Link></li>
        </ul>
    </section>
</div>
</template>