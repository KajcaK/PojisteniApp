document.addEventListener('DOMContentLoaded', function () {
    const loadingElement = document.getElementById('loading');
    loadingElement.style.display = 'block'; // Show loading

    // Fetch statistics data from the API
    fetch('/admin/api/statistics')
        .then(response => response.json())
        .then(data => {
            loadingElement.style.display = 'none'; // Hide loading

            // Prepare data
            const userRolesData = [
                { name: 'Pojištěný', y: data.userStats.ROLE_INSURED },
                { name: 'Pojistník', y: data.userStats.ROLE_POLICYHOLDER },
                { name: 'Admin', y: data.userStats.ROLE_ADMIN },
                { name: 'Registrovaný', y: data.userStats.ROLE_REGISTERED }
            ];

            const userIndividualData = [
                { name: 'Celkem účtů', y: data.userStats.totalUserCount },
                { name: 'Aktivní uživatelé', y: data.userStats.activeUserCount },
                { name: 'Nevyplněné účty', y: data.userStats.usersWithMissingNames }
            ];

            const policyTypeData = [
                { name: 'Povinné ručení', y: data.policyStats.VEHICLE_INSURANCE },
                { name: 'Pojištění majetku', y: data.policyStats.PROPERTY_INSURANCE },
                { name: 'Cestovní pojištění', y: data.policyStats.TRAVEL_INSURANCE },
                { name: 'Životní pojištění', y: data.policyStats.LIFE_INSURANCE },
                { name: 'Pojištění odpovědnosti', y: data.policyStats.LIABILITY_INSURANCE },
                { name: 'Pojištění mazlíčka', y: data.policyStats.PET_INSURANCE },
                { name: 'Úrazové pojištění', y: data.policyStats.ACCIDENT_INSURANCE },
                { name: 'Havarijní pojištění', y: data.policyStats.COLLISION_INSURANCE }
            ];

            const policyTotalVsActiveData = [
                { name: 'Celkem', y: data.policyStats.totalPolicyCount },
                { name: 'Aktivní', y: data.policyStats.totalActivePolicies }
            ];

            const averageCoverageData = [
                {
                    name: 'Průměrná výše krytí',
                    data: [data.avgStats.averagePolicyCoverage ],
                }
            ];

            const eventStatusData = [
                { name: 'čeká na schválení', y: data.eventStats.PENDING_APPROVAL },
                { name: 'nevyplaceno', y: data.eventStats.NOT_PAID },
                { name: 'vyplaceno v celkové hodnotě', y: data.eventStats.FULLY_PAID },
                { name: 'vyplaceno částečně', y: data.eventStats.PARTIALLY_PAID },
                { name: 'zrušeno', y: data.eventStats.CANCELED },
                { name: 'probíhá vyšetřování', y: data.eventStats.UNDER_INVESTIGATION },
                { name: 'pojistná událost uzavřena', y: data.eventStats.CLOSED }
            ];

            const eventTypeData = [
                { name: 'Požár', y: data.eventStats.FIRE },
                { name: 'Povodeň', y: data.eventStats.FLOOD },
                { name: 'Umrtí', y: data.eventStats.DEATH },
                { name: 'Vážná nemoc', y: data.eventStats.SERIOUS_ILLNESS },
                { name: 'Úraz', y: data.eventStats.ACCIDENT },
                { name: 'Trvalé následky', y: data.eventStats.PERMANENT_INJURY },
                { name: 'Zpoždění letu', y: data.eventStats.FLIGHT_DELAY },
                { name: 'Ztráta zavazadel', y: data.eventStats.LUGGAGE_LOSS },
                { name: 'Autonehoda', y: data.eventStats.CAR_ACCIDENT },
                { name: 'Poškození cizího majetku', y: data.eventStats.DAMAGE_TO_THIRD_PARTY_PROPERTY },
                { name: 'Poškození vozidla', y: data.eventStats.VEHICLE_DAMAGE },
                { name: 'Krádež vozidla', y: data.eventStats.VEHICLE_THEFT },
                { name: 'Poškození cizího zdraví', y: data.eventStats.DAMAGE_TO_THIRD_PARTY_HEALTH },
                { name: 'Vandalismus', y: data.eventStats.VANDALISM },
                { name: 'Úraz zvířete', y: data.eventStats.PET_INJURY },
                { name: 'Nemoc zvířete', y: data.eventStats.PET_ILLNESS }
            ];

            const eventAmountsData = [
                { name: 'Celková částka krytí událostí', y: data.eventStats.totalOriginalClaimAmountEvents },
                { name: 'Celkem vyplaceno', y: data.eventStats.totalAmountPaid }
            ];

            // Create charts
            //Users
            const roleChart = new ApexCharts(document.querySelector("#roleChart"), {
                chart: { type: 'donut' },
                theme: { palette: 'palette9' },
                series: userRolesData.map(item => item.y),
                labels: userRolesData.map(item => item.name),
                dataLabels: { enabled: true }
            });

            const userIndividualChart = new ApexCharts(document.querySelector("#userIndividualChart"), {
                chart: { type: 'bar' },

                series: [{
                    name: 'Počet',
                    data: userIndividualData.map(item => item.y)
                }],
                xaxis: {
                    categories: userIndividualData.map(item => item.name),
                },
                colors: ['#5A2A27','#5C4742','#8D5B4C'],
                dataLabels: { enabled: true },
            });

            //Policies
            const policyTypeChart = new ApexCharts(document.querySelector("#policyTypeChart"), {
                chart: { type: 'bar' },
                series: [{ data: policyTypeData.map(item => item.y) }],
                xaxis: {
                        categories: policyTypeData.map(item => item.name)
                    },
                colors: ['#2E294E'],
            });

            const policyTotalVsActiveChart = new ApexCharts(document.querySelector("#policyTotalVsActiveChart"), {
                chart: { type: 'pie' },
                series: policyTotalVsActiveData.map(item => item.y),
                labels: policyTotalVsActiveData.map(item => item.name),
                title: {
                    text: 'Celkem vs Aktivní pojistné smlouvy',
                },
                dataLabels: {
                        enabled: true,
                        formatter: function(val, opts) {
                            const label = opts.w.globals.labels[opts.seriesIndex];
                            return `${label}: ${val.toFixed(2)}`;
                        }
                    },
                    colors: ['#662E9B', '#546E7A'],
            });

            //Events
            const eventStatusChart = new ApexCharts(document.querySelector("#eventStatusChart"), {
                chart: { type: 'bar' },
                series: [{
                    data: eventStatusData.map(item => item.y)
                }],
                xaxis: {
                    categories: eventStatusData.map(item => item.name)
                },
                colors: ['#2b908f'],
            });

            const eventTypeChart = new ApexCharts(document.querySelector("#eventTypeChart"), {
                chart: { type: 'bar' },
                series: [{
                    name: 'Události podle typu',
                    data: eventTypeData.map(item => item.y)
                }],
                xaxis: {
                    categories: eventTypeData.map(item => item.name),
                    labels: {
                        rotate: -45,
                    },
                },
                title: {
                    text: 'Typ',
                },
                colors: ['#546E7A'],
                dataLabels: { enabled: true },
                plotOptions: {
                    bar: { horizontal: true }
                },
            });

            const eventAmountsChart = new ApexCharts(document.querySelector("#eventAmountsChart"), {
                chart: { type: 'pie' },
                series: eventAmountsData.map(item => item.y),
                labels: eventAmountsData.map(item => item.name),
                title: {
                    text: 'Celková hodnota vs Vyplaceno',
                },
                dataLabels: {
                        enabled: true,
                        formatter: function(val, opts) {
                            const label = opts.w.globals.labels[opts.seriesIndex];
                            return `${label}: ${val.toFixed(2)}`;
                        }
                    },
                    colors: ['#5653FE', '#1B998B'],
                });

            const coverageChart = new ApexCharts(document.querySelector("#coverageChart"), {
                chart: {
                    type: 'radialBar',
                    width: '100%',
                },
                series: averageCoverageData[0].data.map(val => val),
                labels: ['CZK'],
                plotOptions: {
                    radialBar: {
                        dataLabels: {
                            name: {
                                fontSize: '15px',
                            },
                            value: {
                                fontSize: '15px',
                                formatter: function(val) {
                                    return `${val.toFixed(2)}`;
                                }
                            }
                        }
                    }
                },
                colors: ['#2E294E'],
            });


            //Populate lists
            const roleList = document.getElementById('roleList');
            userRolesData.forEach(role => {
                const listItem = document.createElement('li');
                listItem.classList.add('list-group-item', 'd-flex', 'justify-content-between', 'align-items-center');
                listItem.innerHTML = `<strong><i class="bi bi-person me-2"></i>${role.name}: </strong> <span>${role.y}</span>`;
                roleList.appendChild(listItem);
            });


            // Render the charts
            roleChart.render();
            userIndividualChart.render();
            policyTypeChart.render();
            policyTotalVsActiveChart.render();
            eventStatusChart.render();
            eventTypeChart.render();
            eventAmountsChart.render();
            coverageChart.render();
            durationChart.render();

        })
        .catch(error => {
            console.error('Error fetching statistics:', error);
            loadingElement.style.display = 'none'; // Hide loading
        });
});
