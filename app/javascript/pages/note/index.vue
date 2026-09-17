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
        <h1>Note List</h1>
        <ul>
            <li><Link href="/note/create">New</Link></li>
            <li><Link href="/note/show">Show</Link></li>
        </ul>
    </section>
</div>
</template>