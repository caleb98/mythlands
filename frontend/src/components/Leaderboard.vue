<template>
    <div class="framed p-3 position-relative">
        <button type="button" class="btn btn-primary p-1" id="leaderboard-refresh" @click="displayLeaderboard(0)">
            <span class="material-icons-round align-middle">
                refresh
            </span>
        </button>
        <div class="row">
            <div class="col-12">
                <h5>Hall of Fame</h5>
                <table class="table">
                    <thead>
                        <tr>
                            <th scope="col">Rank</th>
                            <th scope="col">Name</th>
                            <th scope="col">Level</th>
                            <th scope="col">XP</th>
                            <th scope="col">Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="character in display" :key="character.rank">
                            <th scope="row">{{character.rank}}</th>
                            <td>{{character.firstName}} {{character.lastName}} (<em>{{character.username}}</em>)</td>
                            <td>{{character.level}}</td>
                            <td>{{character.xp}}</td>
                            <td>{{character.isDeceased ? 'Dead' : 'Living'}}</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</template>

<script>
import $ from 'jquery';

export default {
    name: 'Leaderboard',
    props: {
        pageSize: Number
    },

    data() {
        return {
            characterCache: new Map(),
            display: []
        }
    },

    methods: {
        displayLeaderboard(page) {
            // Check if the data is already stored in the cache
            if(this.characterCache.has(page * this.pageSize)) {
                this.display = [];
                for(let i = 0; i < this.pageSize; i++) {
                    display.push(this.characterCache.get(this.pageSize * page + i));
                }
                return;
            }

            // If the data wasn't in the cache, then request it from the server.
            const self = this;
            $.get("/api/halloffame?pageNum=" + page, function(data) {
                if(data.isError) {
                    console.log("Error retrieving leaderboard data: " + data.message);
                    return;
                }

                data = data.data.entries;
                for(const character of data) {
                    self.characterCache.set(character.rank, character);
                }

                self.display = [];
                for(let i = 0; i < self.pageSize; i++) {
                    // Check that the data is actually set in case there were not enough 
                    // players for this leaderboard page.
                    if(!self.characterCache.has(self.pageSize * page + i + 1)) {
                        break;
                    }

                    // Data was set, so keep pushing!
                    self.display.push(self.characterCache.get(self.pageSize * page + i + 1));
                }
            });
        },
    },

    mounted() {
        this.displayLeaderboard(0);
    }
}
</script>

<style scoped>
#leaderboard-refresh {
    position: absolute;
    right: 3px;
    top: 3px;
}
</style>