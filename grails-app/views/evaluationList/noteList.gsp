<g:set var="entities" value="${message(code: 'evaluationListEmployeeNote.entities', default: 'evaluationListEmployeeNote List')}"/>
<g:set var="entity" value="${message(code: 'evaluationListEmployeeNote.entity', default: 'evaluationListEmployeeNote List')}"/>
<g:set var="dataTableTitle" value="${message(code: 'default.list.label', args: [entities], default: 'evaluationListEmployeeNote List')}"/>


<el:modal isModalWithDiv="true" title="${dataTableTitle}" width="90%">
<msg:page/>

<lay:collapseWidget id="evaluationListEmployeeNoteCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <el:modalLink preventCloseOutSide="true" class="btn btn-sm btn-info2 width-135"
                          link="${createLink(controller: 'evaluationList', action: 'noteCreate',id:id)}"
                          label="${message(code: 'default.button.create.label')}">
                <i class="icon-plus"></i>
            </el:modalLink>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="evaluationListEmployeeNoteSearchForm">
            <el:hiddenField name="evaluationListEmployeeId" value="${id}"/>
            <g:render template="noteSearch" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['employeeNoteTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('evaluationListEmployeeNoteSearchForm');_dataTables['employeeNoteTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>

<el:dataTable id="employeeNoteTable"
              searchFormName="evaluationListEmployeeNoteSearchForm"
              dataTableTitle="${dataTableTitle}"
              hasCheckbox="true"
              widthClass="col-sm-12"
              controller="evaluationListEmployeeNote"
              spaceBefore="true"
              hasRow="true"
              action="filter"
              serviceName="evaluationListEmployeeNote">
<el:dataTableAction controller="evaluationListEmployeeNote" action="delete" actionParams="encodedId"
class="red icon-trash" type="confirm-delete"
message="${message(code: 'default.delete.label', args: [entity], default: 'delete evaluationListEmployeeNote')}"/>
</el:dataTable>

</el:modal>
<script type="text/javascript">
    gui.dataTable.initialize($('#application-modal-main-content'));
    gui.modal.initialize($('#application-modal-main-content'));
</script>