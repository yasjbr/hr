<g:set var="entity"
       value="${message(code: 'loanNominatedEmployeeNote.entity', default: 'loanNominatedEmployeeNote')}"/>
<g:set var="entities"
       value="${message(code: 'loanNominatedEmployeeNote.entities', default: 'loanNominatedEmployeeNote List')}"/>
<g:set var="dataTableTitle"
       value="${message(code: 'default.list.label', args: [entities], default: 'loanNominatedEmployeeNote List')}"/>


<el:modal isModalWithDiv="true" title="${dataTableTitle}" width="90%">
    <msg:page/>

    <lay:collapseWidget id="loanNominatedEmployeeNoteCollapseWidget" icon="icon-search"
                        title="${message(code:'default.search.label',args:[entities])}"
                        size="12" collapsed="true" data-toggle="collapse" >
        <lay:widgetToolBar>
            <btn:buttonGroup>
                <el:modalLink preventCloseOutSide="true" class="btn btn-sm btn-info2 width-135"
                              link="${createLink(controller: 'loanNoticeReplayList', action: 'noteCreate',params: [encodedId:encodedId])}"
                              label="${message(code: 'default.button.create.label')}">
                    <i class="icon-plus"></i>
                </el:modalLink>
            </btn:buttonGroup>
        </lay:widgetToolBar>
        <lay:widgetBody>
            <el:form action="#" name="loanNominatedEmployeeNoteSearchForm">
                <el:hiddenField name="loanNominatedEmployee.encodedId" value="${encodedId}"/>
                <g:render template="noteSearch" model="[:]"/>
                <el:formButton functionName="search" onClick="_dataTables['loanNominatedEmployeeNoteTable'].draw()"/>
                <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('loanNominatedEmployeeNoteSearchForm');_dataTables['loanNominatedEmployeeNoteTable'].draw();"/>
            </el:form>
        </lay:widgetBody>
    </lay:collapseWidget>

    <el:dataTable id="loanNominatedEmployeeNoteTable"
                  searchFormName="loanNominatedEmployeeNoteSearchForm"
                  dataTableTitle="${dataTableTitle}"
                  hasCheckbox="true"
                  widthClass="col-sm-12"
                  controller="loanNominatedEmployeeNote"
                  spaceBefore="true"
                  hasRow="true"
                  action="filter"
                  serviceName="loanNominatedEmployeeNote">
        <el:dataTableAction controller="loanNominatedEmployeeNote" action="delete" actionParams="encodedId"
                            class="red icon-trash" type="confirm-delete"
                            message="${message(code: 'default.delete.label',args: [entity], default: 'delete loanNominatedEmployeeNote')}"/>
    </el:dataTable>
</el:modal>
<script type="text/javascript">
    gui.dataTable.initialize($('#application-modal-main-content'));
    gui.modal.initialize($('#application-modal-main-content'));
</script>