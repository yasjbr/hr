<script>
    var index = 1;
    var employeeIdList = [];
    /*in case: create/edit vacation request */
    function vacationTransferValueSettings(checkbox) {
        if (checkbox.checked == true) {
            $('#internalDiv').hide();
            $('#securityCoordination').show();
            $('#row-row-0').remove();
            $('#row-row-1').remove();

            /**
             * represent empty row
             *  @type {string}
             */
            var rowTable = "";
            rowTable += "<tr class='center' id='row-row-0' >";
            rowTable += "<td class='center'>" + "${g.message(code:'vacationRequest.table.empty.message')}" + "</td>";
            rowTable += "<td class='center'></td>";
            rowTable += "<td class='center'></td>";
            rowTable += "<td class='center'></tr>";
            /**
             * insert row into table
             */
            $("#securityCoordinationTable").append(rowTable);


        } else {
            $('#securityCoordination').hide();
            $('#internalDiv').show();

            $('#row-row-0').remove();
            $('#row-row-1').remove();
        }
    }

    /*in case: edit vacation request & internal is true*/
    $(document).ready(function () {
        if ($("#external").val() == 'false') {
            gui.formValidatable.removeRequiredField('vacationRequestForm', 'securityCoordination');
            $('#securityCoordination').hide();
        }
    })


    function successCallBack(json) {
        if (json.success == true) {
            guiLoading.show();
            window.location.href = "${createLink(controller: 'vacationRequest',action: 'createNewVacationRequest')}?employee.id=" + json.data.employee.id + "&vacationType.id=" + json.data.vacationType.id + "&currentBalance=" + json.data.currentBalance + "&external=" + json.data.external;
        }
    }


    /**
     * to get only borders security coordination for selected employee
     */
    function returnEmployeeId() {
        var searchParams = {};
        searchParams['employee.id'] = "${vacationRequest?.employee?.id}";
        return searchParams;
    }

    /**
     * to get only employee with status COMMITTED
     */
    function employeeParams() {
        var searchParams = {};
        searchParams.categoryStatusId = "${ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatusCategory.COMMITTED.toString()}";
        searchParams["idsToExclude[]"] = employeeIdList; // to exclude the selected employee
        return searchParams;
    }


    /**
     * set contact type &  method
     */
    $(document).ready(function () {
        $(window).load(function () {
            /**
             * set contact type to PERSONAL
             */
            var newOption = new Option("${ps.police.pcore.enums.v1.ContactType.PERSONAL}", "${ps.police.pcore.enums.v1.ContactType.PERSONAL.value()}", true, true);
            $('#contactTypeId').append(newOption);
            $('#contactTypeId').trigger('change');
            /**
             * set contact method to  OTHER_ADDRESS
             */
            var newOption = new Option("${ps.police.pcore.enums.v1.ContactMethod.OTHER_ADDRESS}", "${ps.police.pcore.enums.v1.ContactMethod.OTHER_ADDRESS.value()}", true, true);
            $('#contactMethodId').append(newOption);
            $('#contactMethodId').trigger('change');
        });

        /**
         * add selected employee to table
         */
        /*$("#employeeAutoComplete").on("select2:select", function (e) {


            var id = $("#employeeAutoComplete").val();
            var name = $("#employeeAutoComplete option:selected").text();
            var checkDuplicate = 0;
            employeeIdList.forEach(function (entry) {
                if (entry == id) {
                    checkDuplicate++;
                }
            });

            if (checkDuplicate == 0) {
                var rowTable = "<li id='row-" + index + "' class='well-sm alert alert-success' >";
                rowTable += "<span><input type='hidden' name='employeeIdList'  value='" + id + "'>" +
                        name +
                        "</span><span class='delete-action left'><a style='cursor: pointer;' class='red  icon-cancel-3  ' " +
                        "onclick='deleteRow(" + index + ")' " +
                        "title='<g:message code='default.button.delete.label'/>'></a> </span>"
                        + "</li>";
                index++;
                $("#employeeList").append(rowTable);
                employeeIdList.push(id);
            }
        });*/
    });

    function deleteRow(index) {
        //gui.confirm.confirmFunc("${message(code:'default.confirmTitle.label')}", "${message(code:'default.confirm.label')}", function () {
            $('#row-' + index).remove();
            employeeIdList[index - 1] = 0;
        //});
    }


    function addRelatedEmployee(){
        var id = $("#employeeAutoComplete").val();
        var name = $("#employeeAutoComplete option:selected").text();
        var checkDuplicate = 0;
        employeeIdList.forEach(function (entry) {
            if (entry == id) {
                checkDuplicate++;
            }
        });

        if (checkDuplicate == 0) {
            var rowTable = "<li id='row-" + index + "' class='well-sm alert alert-success' >";
            rowTable += "<span><input type='hidden' name='employeeIdList'  value='" + id + "'>" +
                    name +
                    "</span>"
                    + "<button type='button' class='close' href='#'" + "onclick='deleteRow(" + index + ")'>X</button>" + "</li>";
            index++;
            $("#employeeList").append(rowTable);
            employeeIdList.push(id);
        }
        $("#employeeAutoComplete").val('');
        $('#employeeAutoComplete').trigger('change');
    }

    /**
     * to get security coordination for selected employee
     */
    function getSecurityCoordination() {

        /**
         * check if there is a selected multiple rows
         *
         */
        if (_dataTablesCheckBoxValues['employeeSecurityCoordination'].length > 1) {
            var msg = "<div class='alert alert-block alert-danger'>" +
                    "<button data-dismiss='alert' class='close' type='button'>" +
                    "<i class='ace-icon fa fa-times'>" +
                    "</i>" +
                    "</button>" +
                    "<ul>" +
                    "<li>" + "${g.message(code:'vacationRequest.error.oneSelect.securityCoordination.message')}" + "</li> " +
                    "</ul>" +
                    "</div>";
            $('.alert.modalPage').html(msg);
        }

        else if (_dataTablesCheckBoxValues['employeeSecurityCoordination'][0]) {
            $.ajax({
                url: '${createLink(controller: 'bordersSecurityCoordination',action: 'filter')}',
                type: 'POST',
                data: {id: _dataTablesCheckBoxValues['employeeSecurityCoordination'][0]},
                dataType: 'json',
                beforeSend: function (jqXHR, settings) {
                    guiLoading.show();
                },
                error: function (jqXHR) {
                    guiLoading.hide();
                },
                success: function (json) {

                    /**
                     * remove old security coordination
                     */
                    $('#row-row-0').remove();
                    $('#row-row-1').remove();

                    /**
                     * represent new security coordination in formal way
                     * @type {string}
                     */
                    var rowTable = "";
                    rowTable += "<tr class='center' id='row-row-0' >";
                    rowTable += "<td class='center'>" + json.data[0].transientData.documentTypeDTO.descriptionInfo.localName;
                    rowTable += "<input type='hidden' name='securityCoordination.id'  value='" + json.data[0].id + "'></td>";
                    rowTable += "<td class='center'>" + json.data[0].transientData.borderCrossingPointDTO.descriptionInfo.localName + "</td>";
                    rowTable += "<td class='center'>" + json.data[0].fromDate + "</td>";
                    rowTable += "<td class='center'>" + json.data[0].toDate + "</td></tr>";
                    /**
                     * insert row into table
                     */
                    $("#securityCoordinationTable").append(rowTable);


                    /**
                     * hide  modal &  GUI loading.....
                     */
                    guiLoading.hide();
                    $('.alert.modalPage').html("");
                    $("#application-modal-main-content").modal('hide');
                }
            });

        } else {
            var msg = "<div class='alert alert-block alert-danger'>" +
                    "<button data-dismiss='alert' class='close' type='button'>" +
                    "<i class='ace-icon fa fa-times'>" +
                    "</i>" +
                    "</button>" +
                    "<ul>" +
                    "<li>" + "${g.message(code:'vacationRequest.error.select.securityCoordination.message')}" + "</li> " +
                    "</ul>" +
                    "</div>";
            $('.alert.modalPage').html(msg);
        }


    }


    /**
     * calculate the number of days for vacation
     */
    function calculateNumberOfDays() {
        /**
         * get from & to date values
         */
        var fromDate = $("#fromDate").val();
        var toDate = $("#toDate").val();


        /**
         * validate from & to date exist
         */
        if (fromDate && toDate) {

            /**
             * split from & to date
             * @type {Array}
             */
            var parts1 = fromDate.split("/");
            var parts2 = toDate.split("/");

            /**
             * create new from & to date
             * @type {Date}
             */
            var date1 = new Date(parseInt(parts1[2], 10),
                    parseInt(parts1[1], 10) - 1,
                    parseInt(parts1[0], 10));

            var date2 = new Date(parseInt(parts2[2], 10),
                    parseInt(parts2[1], 10) - 1,
                    parseInt(parts2[0], 10));


            /**
             * calculate the number of days
             * @type {number}
             */
            var timeDiff = Math.abs(date2.getTime() - date1.getTime());
            var diffDays = Math.ceil(timeDiff / (1000 * 3600 * 24)) + 1;

            /**
             * change label value to new value
             * @type {number}
             */
            document.getElementById("numberOfDays").innerHTML = diffDays;
        }


    }



</script>