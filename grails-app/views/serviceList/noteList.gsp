<g:set var="entities" value="${message(code: 'serviceListEmployeeNote.entities', default: 'serviceListEmployeeNote List')}"/>
<g:set var="entity" value="${message(code: 'serviceListEmployeeNote.entity', default: 'serviceListEmployeeNote List')}"/>
<g:set var="dataTableTitle" value="${message(code: 'default.list.label', args: [entities], default: 'serviceListEmployeeNote List')}"/>


<el:modal isModalWithDiv="true" title="${dataTableTitle}" width="90%">
    <msg:page/>

    <lay:collapseWidget id="serviceListEmployeeNoteCollapseWidget" icon="icon-search"
                        title="${message(code: 'default.search.label', args: [entities])}"
                        size="12" collapsed="true" data-toggle="collapse">
        <lay:widgetToolBar>
            <btn:buttonGroup>
                <el:modalLink preventCloseOutSide="true" class="btn btn-sm btn-info2 width-135"
                              link="${createLink(controller: 'serviceList', action: 'noteCreate', id: id)}"
                              label="${message(code: 'default.button.create.label')}">
                    <i class="icon-plus"></i>
                </el:modalLink>
            </btn:buttonGroup>
        </lay:widgetToolBar>
        <lay:widgetBody>
            <el:form action="#" name="serviceListEmployeeNoteSearchForm">
                <el:textField name="serviceListEmployee.id" value="${id}" size="6" label="id"/>
                <g:render template="noteSearch" model="[:]"/>
                <el:formButton functionName="search" onClick="_dataTables['employeeNoteTable'].draw()"/>
                <el:formButton functionName="clear"
                               onClick="gui.formValidatable.resetForm('serviceListEmployeeNoteSearchForm');_dataTables['employeeNoteTable'].draw();"/>
            </el:form>
        </lay:widgetBody>
    </lay:collapseWidget>

    <el:dataTable id="employeeNoteTable"
                  searchFormName="serviceListEmployeeNoteSearchForm"
                  dataTableTitle="${dataTableTitle}"
                  hasCheckbox="true"
                  widthClass="col-sm-12"
                  controller="serviceListEmployeeNote"
                  spaceBefore="true"
                  hasRow="true"
                  action="filter"
                  serviceName="serviceListEmployeeNote">
        <el:dataTableAction controller="serviceListEmployeeNote" action="delete" actionParams="encodedId"
                            class="red icon-trash" type="confirm-delete"
                            message="${message(code: 'default.delete.label', args: [entity], default: 'delete serviceListEmployeeNote')}"/>
    </el:dataTable>
</el:modal>

<script type="text/javascript">
    gui.dataTable.initialize($('#application-modal-main-content'));
    gui.modal.initialize($('#application-modal-main-content'));
</script>