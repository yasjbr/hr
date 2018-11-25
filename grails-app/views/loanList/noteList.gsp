<g:set var="entity"
       value="${message(code: 'loanListPersonNote.entity', default: 'loanListPersonNote')}"/>
<g:set var="entities"
       value="${message(code: 'loanListPersonNote.entities', default: 'loanListPersonNote List')}"/>
<g:set var="dataTableTitle"
       value="${message(code: 'default.list.label', args: [entities], default: 'loanListPersonNote List')}"/>


<el:modal isModalWithDiv="true" title="${dataTableTitle}" width="90%">
    <msg:page/>

    <lay:collapseWidget id="loanListPersonNoteCollapseWidget" icon="icon-search"
                        title="${message(code:'default.search.label',args:[entities])}"
                        size="12" collapsed="true" data-toggle="collapse" >
        <lay:widgetToolBar>
            <btn:buttonGroup>
                <el:modalLink preventCloseOutSide="true" class="btn btn-sm btn-info2 width-135"
                              link="${createLink(controller: 'loanList', action: 'noteCreate',params: [encodedId:encodedId])}"
                              label="${message(code: 'default.button.create.label')}">
                    <i class="icon-plus"></i>
                </el:modalLink>
            </btn:buttonGroup>
        </lay:widgetToolBar>
        <lay:widgetBody>
            <el:form action="#" name="loanListPersonNoteSearchForm">
                <el:hiddenField name="loanListPerson.encodedId" value="${encodedId}"/>
                <g:render template="noteSearch" model="[:]"/>
                <el:formButton functionName="search" onClick="_dataTables['personNoteTable'].draw()"/>
                <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('loanListPersonNoteSearchForm');_dataTables['personNoteTable'].draw();"/>
            </el:form>
        </lay:widgetBody>
    </lay:collapseWidget>

    <el:dataTable id="personNoteTable"
                  searchFormName="loanListPersonNoteSearchForm"
                  dataTableTitle="${dataTableTitle}"
                  hasCheckbox="true"
                  widthClass="col-sm-12"
                  controller="loanListPersonNote"
                  spaceBefore="true"
                  hasRow="true"
                  action="filter"
                  serviceName="loanListPersonNote">
        <el:dataTableAction controller="loanListPersonNote" action="delete" actionParams="encodedId"
                            class="red icon-trash" type="confirm-delete"
                            message="${message(code: 'default.delete.label',args: [entity], default: 'delete loanListPersonNote')}"/>
    </el:dataTable>
</el:modal>
<script type="text/javascript">
    gui.dataTable.initialize($('#application-modal-main-content'));
    gui.modal.initialize($('#application-modal-main-content'));
</script>