<el:dataTable id="personRelationShipsTable"
              searchFormName="personRelationShipsSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12"
              controller="personRelationShips"
              spaceBefore="true" hasRow="true"
              action="filter"
              serviceName="personRelationShips"
              domainColumns="${domainColumns}">

    <g:if test="${isInLineActions}">
        <el:dataTableAction accessUrl="${createLink(controller: 'personRelationShips',action: 'show')}" functionName="renderInLineShow" type="function" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show personRelationShips')}"/>
        <el:dataTableAction accessUrl="${createLink(controller: 'personRelationShips',action: 'edit')}" functionName="renderInLineEdit" type="function" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity], default: 'edit personRelationShips')}"/>

    </g:if>
    <g:else>
        <el:dataTableAction controller="personRelationShips" action="show" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show personRelationShips')}"/>
        <el:dataTableAction controller="personRelationShips" action="edit" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity],default: 'edit personRelationShips')}"/>
    </g:else>
    <el:dataTableAction controller="personRelationShips" action="delete" class="red icon-trash" type="confirm-delete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete personRelationShips')}"/>
</el:dataTable>