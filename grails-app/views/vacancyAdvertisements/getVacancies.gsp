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
            <g:render template="/vacancyAdvertisements/searchForVacanciesModal"/>
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
</script>
