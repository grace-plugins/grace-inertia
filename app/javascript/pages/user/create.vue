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
        <h1>New User</h1>
        <ul>
            <li><Link href="/user/index">List</Link></li>
        </ul>
    </section>
</div>
</template>