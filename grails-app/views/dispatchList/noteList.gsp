<g:set var="entities"
       value="${message(code: 'dispatchListEmployeeNote.entities', default: 'dispatchListEmployeeNote List')}"/>
<g:set var="entity" value="${message(code: 'dispatchListEmployeeNote.entity', default: 'dispatchListEmployeeNote')}"/>
<g:set var="dataTableTitle"
       value="${message(code: 'default.list.label', args: [entities], default: 'dispatchListEmployeeNote List')}"/>


<el:modal isModalWithDiv="true" title="${dataTableTitle}" width="90%">
<msg:page/>

<lay:collapseWidget id="dispatchListEmployeeNoteCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <el:modalLink preventCloseOutSide="true" class="btn btn-sm btn-info2 width-135"
                          link="${createLink(controller: 'dispatchList', action: 'noteCreate',id:id)}"
                          label="${message(code: 'default.button.create.label')}">
                <i class="icon-plus"></i>
            </el:modalLink>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="dispatchListEmployeeNoteSearchForm">
            <el:hiddenField name="dispatchListEmployeeId" value="${id}"/>
            <g:render template="noteSearch" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['employeeNoteTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('dispatchListEmployeeNoteSearchForm');_dataTables['employeeNoteTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>

<el:dataTable id="employeeNoteTable"
              searchFormName="dispatchListEmployeeNoteSearchForm"
              dataTableTitle="${dataTableTitle}"
              hasCheckbox="true"
              widthClass="col-sm-12"
              controller="dispatchListEmployeeNote"
              spaceBefore="true"
              hasRow="true"
              action="filter"
              serviceName="dispatchListEmployeeNote">
%{--<el:dataTableAction controller="dispatchListEmployeeNote" action="show" actionParams="encodedId"--}%
%{--class="green icon-eye"--}%
%{--message="${message(code: 'default.show.label', args: [entity], default: 'show dispatchListEmployeeNote')}"/>--}%
%{--<el:dataTableAction controller="dispatchListEmployeeNote" action="edit" actionParams="encodedId"--}%
%{--class="blue icon-pencil"--}%
%{--message="${message(code: 'default.edit.label', args: [entity], default: 'edit dispatchListEmployeeNote')}"/>--}%
<el:dataTableAction controller="dispatchListEmployeeNote" action="delete" actionParams="encodedId"
class="red icon-trash" type="confirm-delete"
message="${message(code: 'default.delete.label', args: [entity], default: 'delete dispatchListEmployeeNote')}"/>
</el:dataTable>
</el:modal>
<script type="text/javascript">
    gui.dataTable.initialize($('#application-modal-main-content'));
    gui.modal.initialize($('#application-modal-main-content'));
</script>