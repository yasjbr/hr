<g:set var="entities"
       value="${message(code: 'vacancy.entities', default: 'vacancy List')}"/>
<g:set var="dataTableTitle"
       value="${message(code: 'default.list.label', args: [entities], default: 'vacancy List')}"/>


<el:modal isModalWithDiv="true" title="${dataTableTitle}" width="90%" name="securityCoordinationModal"
          id="securityCoordinationModal">
    <msg:modal/>
    <lay:collapseWidget id="vacancyCollapseWidget" icon="icon-search"
                        title="${message(code: 'default.search.label', args: [entities])}"
                        size="12" collapsed="true" data-toggle="collapse">
        <lay:widgetBody>
            <el:form action="#" name="vacancyForm">
                <g:render template="/vacancy/searchForModal"/>
                <el:formButton functionName="search" onClick="_dataTables['vacancyTable'].draw()"/>
                <el:formButton functionName="clear"
                               onClick="gui.formValidatable.resetForm('vacancyForm');_dataTables['vacancyTable'].draw();"/>
            </el:form>
        </lay:widgetBody>
    </lay:collapseWidget>
    <el:dataTable id="vacancyTable"
                  searchFormName="vacancyForm"
                  dataTableTitle="${dataTableTitle}"
                  hasCheckbox="true"
                  widthClass="col-sm-12"
                  controller="vacancy"
                  spaceBefore="true"
                  hasRow="true"
                  action="filter"
                  serviceName="vacancy" domainColumns="DOMAIN_TAB_COLUMNS">
    </el:dataTable>


    <el:modalButton class="btn  btn-bigger  btn-sm  btn-primary  btn-round"
                    icon="ace-icon fa fa-floppy-o"
                    onClick="getSelectedVacancy()"
                    id="firmDocumentAddBtn"
                    message="${g.message(code: 'applicant.button.select.vacancy.label')}"/>

</el:modal>

<script type="text/javascript">
    gui.dataTable.initialize($('#application-modal-main-content'));
    gui.modal.initialize($('#application-modal-main-content'));


    /**
     * to get selected vacancy
     */
    function getSelectedVacancy() {

        /**
         * check if there is a selected multiple rows
         *
         */
        if (_dataTablesCheckBoxValues['vacancyTable'].length > 1) {
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

        else if (_dataTablesCheckBoxValues['vacancyTable'][0]) {
            $.ajax({
                url: '${createLink(controller: 'vacancy',action: 'filter')}',
                type: 'POST',
                data: {id: _dataTablesCheckBoxValues['vacancyTable'][0]},
                dataType: 'json',
                beforeSend: function (jqXHR, settings) {
                    guiLoading.show();
                },
                error: function (jqXHR) {
                    guiLoading.hide();
                },
                success: function (json) {

                    /**
                     * remove old vacancy
                     */
                    $('#row-0').remove();
                    $('#row-1').remove();

                    /**
                     * represent new selected vacancy in formal way
                     * @type {string}
                     */
                    var rowTable = "";
                    rowTable += "<tr class='center' id='row-0' >";
                    rowTable += "<td class='center'>" + json.data[0].recruitmentCycle;
                    rowTable += "<input hidden type='text' name='vacancy.id'  value='" + json.data[0].id + "'></td>";

                    rowTable += "<td class='center'>" + json.data[0].job.descriptionInfo.localName + "</td>";


                    rowTable += "<td class='center'>" + json.data[0].numberOfPositions + "</td>";

                    rowTable += "<td class='center'>" + json.data[0].vacancyStatus + "</td>" +
                            "" +
                            "</tr>";


                    /**
                     * insert row into table
                     */
                    $("#vacancyTable1").append(rowTable);


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
                    "<li>" + "${g.message(code:'applicant.error.select.vacancy.message')}" + "</li> " +
                    "</ul>" +
                    "</div>";
            $('.alert.modalPage').html(msg);
        }


    }


</script>
