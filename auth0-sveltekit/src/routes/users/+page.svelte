<script lang="ts">
	export let data;
</script>

<h1>This is the users page</h1>

<ul>
	{#each data.users as user}
		<li>{user.nickname}</li>
	{/each}
</ul>
