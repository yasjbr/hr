<g:set var="entities"
       value="${message(code: 'bordersSecurityCoordination.entities', default: 'bordersSecurityCoordination List')}"/>
<g:set var="dataTableTitle"
       value="${message(code: 'default.list.label', args: [entities], default: 'bordersSecurityCoordination List')}"/>


<el:modal isModalWithDiv="true" title="${dataTableTitle}" width="90%" name="securityCoordinationModal"
          id="securityCoordinationModal">
    <msg:modal/>
    <lay:collapseWidget id="bordersSecurityCoordinationCollapseWidget" icon="icon-search"
                        title="${message(code: 'default.search.label', args: [entities])}"
                        size="12" collapsed="true" data-toggle="collapse">
        <lay:widgetBody>
            <el:form action="#" name="employeeSecurityCoordinationSearchForm">
                <g:render template="/bordersSecurityCoordination/searchModal" model="[id: id]"/>
                <el:formButton functionName="search" onClick="_dataTables['employeeSecurityCoordination'].draw()"/>
                <el:formButton functionName="clear"
                               onClick="gui.formValidatable.resetForm('employeeSecurityCoordinationSearchForm');_dataTables['employeeSecurityCoordination'].draw();"/>
            </el:form>
        </lay:widgetBody>
    </lay:collapseWidget>
    <el:dataTable id="employeeSecurityCoordination"
                  searchFormName="employeeSecurityCoordinationSearchForm"
                  dataTableTitle="${dataTableTitle}"
                  hasCheckbox="true"
                  widthClass="col-sm-12"
                  controller="bordersSecurityCoordination"
                  spaceBefore="true"
                  hasRow="true"
                  action="filter"
                  serviceName="bordersSecurityCoordination" domainColumns="VACATION_REQUEST_COLUMNS">
    </el:dataTable>


    <el:modalButton class="btn  btn-bigger  btn-sm  btn-primary  btn-round"
                    icon="ace-icon fa fa-floppy-o"
                    onClick="getSecurityCoordination()"
                    id="firmDocumentAddBtn"
                    message="${g.message(code: 'vacationRequest.selectSecurityCoordination.label')}"/>

</el:modal>

<script type="text/javascript">
    gui.dataTable.initialize($('#application-modal-main-content'));
    gui.modal.initialize($('#application-modal-main-content'));
</script>
