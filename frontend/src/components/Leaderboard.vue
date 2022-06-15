<template>
    <div class="framed p-3 position-relative">
        <button type="button" class="btn btn-primary p-1" id="leaderboard-refresh" @click="refresh()">
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
                            <td>{{character.deceased == null ? '-' : (character.deceased ? 'Dead' : 'Living')}}</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
        <div class="row justify-content-center">
            <div class="col-4">
                <div class="input-group">
                    <button type="button" class="btn btn-primary p-1" @click="prevPage()">
                        <span class="material-icons-round align-middle">
                            chevron_left
                        </span>
                    </button>
                    <input type="number" class="form-control text-center" placeholder="page" :value="displayPage" min="1" 
                        @change="event => updatePage(event.target.value)">
                    <button type="button" class="btn btn-primary p-1" @click="nextPage()">
                        <span class="material-icons-round align-middle">
                            chevron_right
                        </span>
                    </button>
                </div>
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
            display: [],
            currentPage: 0
        }
    },

    methods: {
        displayLeaderboard(page) {
            // Check if the data is already stored in the cache
            if(this.characterCache.has(page * this.pageSize + 1)) {
                this.display = [];
                for(let i = 0; i < this.pageSize; i++) {
                    // Make sure we haven't reached the end of the leaderboard
                    if(!this.characterCache.has(this.pageSize * page + i + 1)) {
                        break;
                    }

                    // Add the character to be displayed
                    this.display.push(this.characterCache.get(this.pageSize * page + i + 1));   
                }

                // Fill empty spots
                this.fillEmpty(page);
                return;
            }

            // If the data wasn't in the cache, then request it from the server.
            const self = this;
            $.get("/api/halloffame?pageNum=" + page + "&pageSize=" + this.pageSize, function(data) {
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

                // Fill empty spots
                self.fillEmpty(page);
            });
        },

        updatePage(displayPage) {
            this.currentPage = displayPage - 1;
            this.displayLeaderboard(this.currentPage);
        },

        nextPage() {
            this.currentPage++;
            this.displayLeaderboard(this.currentPage);
        },

        prevPage() {
            this.currentPage--;
            if(this.currentPage < 0) {
                this.currentPage = 0;
            }
            this.displayLeaderboard(this.currentPage);
        },

        fillEmpty(page) {
            let startRank;
            if(this.display.length == 0) {
                startRank = this.pageSize * page + 1
            }
            else {
                startRank = this.display[this.display.length - 1].rank + 1
            }
            while(this.display.length < this.pageSize) {
                this.display.push({
                    rank: startRank++,
                    firstName: "...",
                    lastName: "...",
                    username: '...',
                    level: '.',
                    xp: '.'
                });
            }
        },

        refresh() {
            this.characterCache = new Map();
            this.displayLeaderboard(this.currentPage);
        }
    },

    mounted() {
        this.displayLeaderboard(this.currentPage);
    },

    computed: {
        displayPage() {
            return this.currentPage + 1;
        }
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