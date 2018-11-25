<script>

    var index = ${(vacancyAdvertisements?.id)?(vacancyAdvertisements?.joinedVacancyAdvertisement?.size()+1):1};

    function  recruitmentCycleParams() {
        var searchParams = {};
        searchParams.requisitionAnnouncementStatus ='${ps.gov.epsilon.hr.enums.v1.EnumRequisitionAnnouncementStatus.ADVERT}';
        return searchParams;
    }

    /**
     * to get selected vacancy
     */
    function getSelectedVacancy() {

        /**
         * check if there is a selected multiple rows
         *
         */
        /*if (_dataTablesCheckBoxValues['vacancyTable'].length > 1) {
         var msg = "<div class='alert alert-block alert-danger'>" +
         "<button data-dismiss='alert' class='close' type='button'>" +
         "<i class='ace-icon fa fa-times'>" +
         "</i>" +
         "</button>" +
         "<ul>" +
         "<li>" + "${g.message(code:'applicant.error.oneSelect.vacancy.message')}" + "</li> " +
         "</ul>" +
         "</div>";
         $('.alert.modalPage').html(msg);
         }

         else if (_dataTablesCheckBoxValues['vacancyTable'][0]) {*/
        if (_dataTablesCheckBoxValues['vacancyTable'].length > 0) {



            //for(var i=0;i < _dataTablesCheckBoxValues['vacancyTable'].length;i++){
            $.ajax({
                url: '${createLink(controller: 'vacancy',action: 'filter')}',
                type: 'POST',
                data: {ids: _dataTablesCheckBoxValues['vacancyTable']},
                dataType: 'json',
                beforeSend: function (jqXHR, settings) {
                    guiLoading.show();
                },
                error: function (jqXHR) {
                    guiLoading.hide();
                },
                success: function (json) {
                    $('#row-0').remove();
                    for(var i=0;i<json.data.length;i++){
                        var rowTable = "";
                        rowTable += "<tr class='center vacancies-rows' id='row-" + index + "'>";
                        rowTable += "<td class='center'>" + json.data[i].recruitmentCycle;
                        rowTable += "<input  type='hidden' name='vacancy'  value='" + json.data[i].id + "'></td>";
                        rowTable += "<td class='center'>" + json.data[i].job.descriptionInfo.localName + "</td>";
                        rowTable += "<td class='center'>" + json.data[i].numberOfPositions + "</td>";
                        rowTable += "<td class='center'>" + json.data[i].vacancyStatus + "</td>";
                        rowTable += "<td class='center'>";
                        rowTable += "<span class='delete-action'><a style='cursor: pointer;' class='red icon-trash ' onclick='deleteRow(" + index + ")' title='<g:message code='default.button.delete.label'/>'></a> </span>";//delete
                        rowTable += "</td></tr>";
                        index++;

                        $("#vacancyTable1").append(rowTable);
                    }
                }
            });
            //}

            /**
             * hide  modal &  GUI loading.....
             */
            guiLoading.hide();
            $('.alert.modalPage').html("");
            $("#application-modal-main-content").modal('hide');
        } else {
            var msg = "<div class='alert alert-block alert-danger'>" +
                    "<button data-dismiss='alert' class='close' type='button'>" +
                    "<i class='ace-icon fa fa-times'>" +
                    "</i>" +
                    "</button>" +
                    "<ul>" +
                    "<li>" + "${g.message(code:'applicant.error.select.vacancy.message')}" + "</li> " +
                    "</ul>" +
                    "</div>";
            $('.alert.modalPage').html(msg);
        }


    }

    function deleteRow(index) {
        gui.confirm.confirmFunc("${message(code:'default.confirmTitle.label')}", "${message(code:'default.confirm.label')}", function () {
            $('#row-' + index).remove();
            resetVacanciesTable();
        });
    }

    function resetVacanciesTable(){
        if($("#vacancyTable1 tbody tr.center").length == 0) {
            var rowTable //= "<rowElement>";
            rowTable += "<tr class='center vacancies-rows' id='row-0' >";
            rowTable += "<td class='center'></td>";
            rowTable += "<td class='center'></td>";
            rowTable += "<td class='center'></td>";
            rowTable += "<td class='center'></td>";
            rowTable += "</tr>";
            $("#vacancyTable1").append(rowTable);
        }
    }

</script>