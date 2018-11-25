<script type="text/javascript">

    /**
     * show/hide divs
     * @param select
     */
    function selectChanged(select) {
        which = $(select).val();

        if (which == "${ps.gov.epsilon.hr.enums.vacation.v1.EnumBalanceCalculationMechanism.ALL_EMPLOYEES}") {
            $("#employeeDiv").hide();

            msg = "<div class='alert alert-block alert-info'>" +
                    " ${message(code: 'employeeVacationBalance.allEmployeesSelection.label', default: 'allEmployee')} " +
                    "<br></div>";
            $('#allEmployeeDiv .alert').html(msg);
            $("#allEmployeeDiv").show();
        } else if (which == "${ps.gov.epsilon.hr.enums.vacation.v1.EnumBalanceCalculationMechanism.ONE_EMPLOYEE}") {
            $('#employeeDiv .alert').html("");
            $("#employeeDiv").show();
            $("#allEmployeeDiv").hide();
        } else {
            $('#employeeDiv .alert').html("");
            $("#employeeDiv").hide();
            $("#allEmployeeDiv").hide();
            $("#employeeVacationsTable").text("");

        }
    }

    /**
     * to get employee vacations balance and represent them in table
     */
    function showEmployeeVacationsBalance() {
        var selectedEmployeeId = $("#employeeId").val();
        var name = $("#employeeId option:selected").text();
        if (selectedEmployeeId != null) {
            $.ajax({
                url: "<g:createLink controller="employeeVacationBalance" action="filter"/>",
                type: 'POST',
                data: $("#mechanismForm,#employeeForm").serialize(),
                beforeSend: function () {
                    guiLoading.show();
                    $('#employeeDiv .alert').html("");
                    $("#employeeVacationsTable").text("");
                    $("#employeeVacationConfigurationDiv").hide();
                },
                success: function (json) {
                    guiLoading.hide();
                    if (json.data.length > 0) {
                        $("#employeeVacationConfigurationDiv .alert").html("${message(code: 'employeeVacationBalance.employeeBalance.label',default: 'employee balance')}" + " : " + name);
                        /**
                         * draw table head
                         **/
                        var rowTable = " <thead>" +
                                "<tr>" +
                                "<th class='center' width='3%'></th> " +
                                "<th class='center'>${message(code: 'employeeVacationBalance.vacationConfiguration.vacationType.descriptionInfo.localName.label')}</th> " +
                                "<th class='center'>${message(code: 'employeeVacationBalance.annualBalance.label')}</th> " +
                                "<th class='center'>${message(code: 'employeeVacationBalance.oldTransferBalance.label')}</th> " +
                                "<th class='center'>${message(code: 'employeeVacationBalance.balance.label')}</th> " +
                                "<th class='center'>${message(code: 'employeeVacationBalance.usedBalance.label')}</th> " +
                                "<th class='center'>${message(code: 'employeeVacationBalance.validFromDate.label')}</th> " +
                                "<th class='center'>${message(code: 'employeeVacationBalance.validToDate.label')}</th> " + +"</tr>" +
                                "</thead>";

                        /**
                         * represent  employee vacations in table
                         */
                        for (i = 0; i < json.data.length; i++) {
                            rowTable += "<tr  class='center' >";
                            rowTable += "<td class='center'><label style='background-color:rgb" + json.data[i].vacationConfiguration.vacationType.transientData.colorDTO.rgbHexa + ";width: 100%;height: 100%;'" + "></label></td>";
                            rowTable += "<td class='center'>" + json.data[i].vacationConfiguration.vacationType.descriptionInfo.localName + "</td>";
                            rowTable += "<td class='center'>" + json.data[i].annualBalance + "</td>";
                            rowTable += "<td class='center'>" + json.data[i].oldTransferBalance + "</td>";
                            rowTable += "<td class='center'>" + json.data[i].balance + "</td>";
                            rowTable += "<td class='center'>" + ( parseInt(json.data[i].oldTransferBalance) + parseInt(json.data[i].annualBalance) - parseInt(json.data[i].balance)) + "</td>";
                            rowTable += "<td class='center'>" + json.data[i].validFromDate + "</td>";
                            rowTable += "<td class='center'>" + json.data[i].validToDate + "</td></tr>";
                            $("#employeeVacationsTable").append(rowTable);
                            rowTable = "";
                        }

                        $("#employeeVacationConfigurationDiv").show();
                    } else {
                        /**
                         * in case there is no vacation record for employee
                         */
                        var msg = "<div class='alert alert-block alert-warning  '>     " +
                                "       <button data-dismiss='alert' class='close' type='button'> " +
                                "               <i class='ace-icon fa fa-times'></i>  " +
                                "          </button>${message (code:'employeeVacationBalance.employeeHasNoBalance.label',default: 'employeeHasNoBalance')}<br></div>'";
                        $('#employeeDiv .alert').html(msg);
                    }

                },
                error: function (request, status, error) {
                    guiLoading.hide();
                }
            });


        } else {
            /**
             * in case user does not select the employee
             */
            var msg = "<div class='alert alert-block alert-danger'>" +
                    "<button data-dismiss='alert' class='close' type='button'>" +
                    "<i class='ace-icon fa fa-times'></i></button>" +
                    "<i class='ace-icon fa fa-times'></i> " +
                    " ${message (code:'employeeVacationBalance.errorSelectEmployee.label',default: 'errorSelectEmployee')}  " +
                    "<br></div>";
            $('#employeeDiv .alert').html(msg);
            $("#employeeVacationsTable").text("");
        }
    }


    /**
     * to calculate employee vacation balance
     */
    function calculateEmployeeYearlyBalance() {
        $("#calculateVacationsBalanceBtn").attr("disabled", 'disabled');
        $.ajax({
            url: '${createLink(controller: 'employeeVacationBalance',action: 'calculateEmployeeYearlyBalance')}',
            type: 'POST',
            data: $("#mechanismForm,#employeeForm").serialize(),
            dataType: 'json',
            beforeSend: function (jqXHR, settings) {
                guiLoading.show();
            },
            error: function (jqXHR) {
                guiLoading.hide();
                $("#calculateVacationsBalanceBtn").removeAttr('disabled');
            },
            success: function (json) {
                guiLoading.hide();
                if (json.success) {
                    showEmployeeVacationsBalance();
                } else {
                    var msg = ""
                    if (json.error) {
                        msg = "<div class='alert alert-block alert-danger'>" +
                                "<button data-dismiss='alert' class='close' type='button'>" +
                                "<i class='ace-icon fa fa-times'></i></button>" +
                                "<i class='ace-icon fa fa-times'></i> " +
                                json.error +
                                "<br></div>";
                    } else {
                        msg = "<div class='alert alert-block alert-danger'>" +
                                "<button data-dismiss='alert' class='close' type='button'>" +
                                "<i class='ace-icon fa fa-times'></i></button>" +
                                "<i class='ace-icon fa fa-times'></i> " +
                                " ${message (code:'employeeVacationBalance.errorSelectEmployee.label',default: 'errorSelectEmployee')}  " +
                                "<br></div>";
                    }

                    $("#employeeVacationConfigurationDiv").hide();
                    $('#employeeDiv .alert').html(msg);
                }
                $("#calculateVacationsBalanceBtn").removeAttr('disabled');
            }
        });
    }


    /**
     * to calculate all employee yearly balance
     */
    function calculateAllEmployeeYearlyBalance() {
        $.ajax({
            url: '${createLink(controller: 'employeeVacationBalance',action: 'calculateAllEmployeeYearlyBalance')}',
            type: 'POST',
            data: $("#mechanismForm").serialize(),
            dataType: 'json',
            beforeSend: function (jqXHR, settings) {
                guiLoading.show();
            },
            error: function (jqXHR) {
                guiLoading.hide();
            },
            success: function (json) {
                var msg = "";
                guiLoading.hide();
                if (json.success) {
                    msg = "<div class='alert alert-block alert-success'>" +
                            "<button data-dismiss='alert' class='close' type='button'>" +
                            "<i class='ace-icon fa fa-times'></i></button>" +
                            "<i class='ace-icon fa fa-times'></i> " +
                            " ${message (code:'employeeVacationBalance.calculationAllEmployeeYearlyBalance.success.message',default: 'success ')}  " +
                            "<br></div>";
                } else {
                    if (json.error) {
                        msg = "<div class='alert alert-block alert-danger'>" +
                                "<button data-dismiss='alert' class='close' type='button'>" +
                                "<i class='ace-icon fa fa-times'></i></button>" +
                                "<i class='ace-icon fa fa-times'></i> " +
                                json.error +
                                "<br></div>";
                    }

                }
                $("#employeeVacationConfigurationDiv").hide();
                $('#allEmployeeDiv .alert').html(msg);
            }
        });
    }


    $(window).ready(function () {
        /*    $("#selectedYear_day").remove();
         $("#selectedYear_month").remove();
         $("#selectedYear_hour").remove();
         $("#selectedYear_minute").remove();*/


        /**
         * when any action happen on employee selection we remove the vacation table
         */

        $("#employeeId").on('select2:select', function (evt) {
            $("#employeeVacationsTable").text("");
            $("#employeeVacationConfigurationDiv .alert").html("");
            $("#employeeVacationConfigurationDiv").hide();
        });
        $("#employeeId").on('select2:selecting', function (evt) {
            $("#employeeVacationsTable").text("");
            $("#employeeVacationConfigurationDiv .alert").html("");
            $("#employeeVacationConfigurationDiv").hide();
        });
        $("#employeeId").on('change', function (evt) {
            $("#employeeVacationsTable").text("");
            $("#employeeVacationConfigurationDiv .alert").html("");
            $("#employeeVacationConfigurationDiv").hide();
        });
        $("#employeeId").on('select2:loaded', function (evt) {
            $("#employeeVacationsTable").text("");
            $("#employeeVacationConfigurationDiv .alert").html("");
            $("#employeeVacationConfigurationDiv").hide();
        });
        $("#employeeId").on('select2:removed', function (evt) {
            $("#employeeVacationsTable").text("");
            $("#employeeVacationConfigurationDiv .alert").html("");
            $("#employeeVacationConfigurationDiv").hide();
        });
        $("#employeeId").on('select2:open', function (evt) {
            $("#employeeVacationsTable").text("");
            $("#employeeVacationConfigurationDiv .alert").html("");
            $("#employeeVacationConfigurationDiv").hide();
        });
    });


    /**
     * to get only employee with status COMMITTED
     */
    function employeeParams() {
        var searchParams = {};
        searchParams.categoryStatusId = "${ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatusCategory.COMMITTED.toString()}";
        return searchParams;
    }


    /**
     * to get employee vacations balance and represent them in table
     */
    function showEmployeeBalance() {
        var selectedEmployeeId = $("#employeeId").val();
        var name = $("#employeeId option:selected").text();

        if (selectedEmployeeId && name) {
            $.ajax({
                url: "<g:createLink controller="employeeVacationBalance" action="filter"/>",
                type: 'POST',
                data: $("#employeeForm").serialize(),
                beforeSend: function () {
                    guiLoading.show();
                    $('.alert').html("");
                    $("#employeeVacationsTable").text("");
                    $("#employeeVacationConfigurationDiv").hide();
                },
                success: function (json) {
                    guiLoading.hide();
                    if (json.data.length > 0) {
                        if (name) {
                            $("#employeeVacationConfigurationDiv .alert").html("${message(code: 'employeeVacationBalance.employeeBalance.label',default: 'employee balance')}" + " : " + name);
                            /**
                             * draw table head
                             **/
                            var rowTable = " <thead>" +
                                    "<tr>" +
                                    "<th class='center' width='3%'></th> " +
                                    "<th class='center'>${message(code: 'employeeVacationBalance.vacationConfiguration.vacationType.descriptionInfo.localName.label')}</th> " +
                                    "<th class='center'>${message(code: 'employeeVacationBalance.annualBalance.label')}</th> " +
                                    "<th class='center'>${message(code: 'employeeVacationBalance.oldTransferBalance.label')}</th> " +
                                    "<th class='center'>${message(code: 'employeeVacationBalance.balance.label')}</th> " +
                                    "<th class='center'>${message(code: 'employeeVacationBalance.usedBalance.label')}</th> " +
                                    "<th class='center'>${message(code: 'employeeVacationBalance.validFromDate.label')}</th> " +
                                    "<th class='center'>${message(code: 'employeeVacationBalance.validToDate.label')}</th> " + +"</tr>" +
                                    "<th class='center'>${message(code: 'default.actions.label')}</th> " + +"</tr>" +
                                    "</thead>";

                            /**
                             * represent  employee vacations balance in table
                             */
                            for (i = 0; i < json.data.length; i++) {
                                rowTable += "<tr  class='center' >";
                                rowTable += "<td class='center'><label style='background-color:rgb" + json.data[i].vacationConfiguration.vacationType.transientData.colorDTO.rgbHexa + ";width: 100%;height: 100%;'" + "></label></td>";
                                rowTable += "<td class='center'>" + json.data[i].vacationConfiguration.vacationType.descriptionInfo.localName + "</td>";
                                rowTable += "<td class='center'>" + json.data[i].annualBalance + "</td>";
                                rowTable += "<td class='center'>" + json.data[i].oldTransferBalance + "</td>";
                                rowTable += "<td class='center'>" + json.data[i].balance + "</td>";
                                rowTable += "<td class='center'>" + ( parseInt(json.data[i].oldTransferBalance) + parseInt(json.data[i].annualBalance) - parseInt(json.data[i].balance)) + "</td>";
                                rowTable += "<td class='center'>" + json.data[i].validFromDate + "</td>";
                                rowTable += "<td class='center'>" + json.data[i].validToDate + "</td>";
                                rowTable += "<td class='center'>" + "<a href='/EPHR/employeeVacationBalance/show?encodedId=" + json.data[i].encodedId +
                                        "' class='green icon-eye  tooltip-success' title=''  data-rel='tooltip' data-original-title='${g.message(code: 'default.show.label',args: ["${g.message(code:'employeeVacationBalance.label')}"],default: 'show employee vacation balance')}' ></td>";
                                rowTable += "</tr>";
                                $("#employeeVacationsTable").append(rowTable);
                                rowTable = "";
                            }

                            $("#employeeVacationConfigurationDiv").show();
                        }
                    } else {
                        /**
                         * in case there is no vacation record for employee
                         */
                        var msg = "<div class='alert alert-block alert-warning  '>     " +
                                "       <button data-dismiss='alert' class='close' type='button'> " +
                                "               <i class='ace-icon fa fa-times'></i>  " +
                                "          </button>${message (code:'employeeVacationBalance.employeeHasNoBalance.label',default: 'employeeHasNoBalance')}<br></div>'";
                        $('.alert').html(msg);
                    }

                },
                error: function (request, status, error) {
                    guiLoading.hide();
                }
            });


        } else {
            $("#employeeVacationConfigurationDiv").hide();
            /**
             * in case user does not select the employee
             */
            var msg = "<div class='alert alert-block alert-danger'>" +
                    "<button data-dismiss='alert' class='close' type='button'>" +
                    "<i class='ace-icon fa fa-times'></i></button>" +
                    "<i class='ace-icon fa fa-times'></i> " +
                    " ${message (code:'employeeVacationBalance.error.select.employee.message',default: 'errorSelectEmployee')}  " +
                    "<br></div>";
            $('.alert').html(msg);
            $("#employeeVacationsTable").text("");
        }
    }

</script>
