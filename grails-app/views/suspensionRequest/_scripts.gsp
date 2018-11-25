<script>

    /**
     * to get only employee with status WORKING
     */
    function employeeParams() {
        var searchParams = {};
        searchParams.categoryStatusId = "${ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatus.WORKING.toString()}";
        return searchParams;
    }

    /**
     * redirect to create suspension request when successful  select employee & suspension type
     * @param json
     */
    function successCallBack(json) {

        if (json.success == true) {
            guiLoading.show();
            window.location.href = "${createLink(controller: 'suspensionRequest',action: 'createNewSuspensionRequest')}?employee.id=" + json.data.employee.id + "&suspensionType=" + json.data.suspensionType.name;
        }
    }

    /**
     * manage stop suspension
     * @param row
     * @returns {boolean}
     */
    function manageStopSuspension(row) {
        /**
         * get local date time
         * @type {string}
         */
        var localDate = "${java.time.ZonedDateTime.now()?.toLocalDate()}";

        /**
         * split date by "-"
         * @type {Array}
         */
        var localDateArray = localDate.split("-");


        /**
         * create day, month and year
         * @type {Number}
         */
        var day = parseInt(localDateArray[2], 10);
        var month = parseInt(localDateArray[1], 10);
        var year = parseInt(localDateArray[0], 10);


        /**
         * set day two digits
         */
        if (day < 10) {
            day = "0" + day;
        }


        /**
         * set month two digits
         */
        if (month < 10) {
            month = "0" + month;
        }

        /**
         * local date with format day/month/year
         * @type {string}
         */
        localDate = day + "/" + month + "/" + year;


        if ((row.requestStatus == "${g.message(code:'EnumRequestStatus.APPROVED')}") && (row.fromDate <= localDate)) {
            return true;
        }
        return false;
    }

    $(document).ready(function () {
        /*
         * on change from date, set toDate value to fromDate's value and year +1
         * */
        $("#fromDate").change(function () {

            /**
             * get from date value
             * @type {any}
             */
            var fromDate = $("#fromDate").val();
            var toDate = $("#toDate").val();


            /**
             * change toDate value if fromDate is exist and toDate dose not exist
             */
            if (fromDate && !toDate) {
                /**
                 * split date and increment year by 1
                 */
                var parts1 = fromDate.split("/");
                var date1 = new Date(parseInt(parts1[2], 10),
                        parseInt(parts1[1], 10) - 1,
                        parseInt(parts1[0], 10));
                var newDate = date1;
                newDate.setFullYear(newDate.getFullYear() + 1);
                var day = newDate.getDate();
                var month = newDate.getMonth() + 1;

                /**
                 * set day two digits
                 */
                if (day < 10) {
                    day = "0" + day;
                }
                /**
                 * set month two digits
                 */
                if (month < 10) {
                    month = "0" + month;
                }

                /**
                 * set to date value
                 * @type {string}
                 */
                var dateFormatted = (day + '/' + month + '/' + newDate.getFullYear());
                $("#toDate").val(dateFormatted)

            }
        });


    });

</script>