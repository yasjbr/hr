
    <div class="col-md-12">
        <table width="100%">
            <tr>
                <td style="width: 100%;">
                    <h4 class=" smaller lighter blue">${message(code: 'aocCorrespondenceList.copyToPartyList.label')}</h4>
                </td>
                <g:if test="${showActions}">
                    <td width="160px" align="left">
                        <el:modalLink preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                                      link="${createLink(controller: 'aocCorrespondenceList', action: 'addCopyToModal')}"
                                      label="${message(code: 'aocCorrespondenceList.copyToParty.add.btn')}">
                            <i class="ace-icon fa fa-plus-square-o"></i>
                        </el:modalLink>
                    </td>
                </g:if>
            </tr>
        </table>
        <hr style="margin-top: 0px;margin-bottom: 9px;"/>
    </div>

    <el:formGroup>
        <div class="col-md-12" id="tableDiv">
            <lay:table styleNumber="1" id="copyToPartyTable">
                <lay:tableHead title="${message(code: 'default.index.label')}"/>
                <lay:tableHead title="${message(code: 'aocCorrespondenceList.COPY.class.label')}"/>
                <lay:tableHead title="${message(code: 'aocCorrespondenceList.COPY.name.label')}"/>
                <g:if test="${showActions}">
                    <lay:tableHead title="${message(code: 'default.actions.label')}"/>
                </g:if>
                    <g:each in="${copyToPartyList}" var="copyParty"
                        status="index">
                    <rowElement>
                        <tr id="row-${index + 1}" class='center'>
                            <td class='center'>${index+1}</td>
                            <td class='center'>${message(code:'EnumCorrespondencePartyClass.'+copyParty?.partyClass)}</td>
                            <td class='center'>${copyParty?.name}</td>
                            <g:if test="${showActions}">
                                <td class='center'>
                                    <input type='hidden' name='partyTypeCopy' id='partyType-${index + 1}'
                                           value='${copyParty?.partyType}'/>
                                    <input type='hidden' name='partyClassCopy' id='partyClass-${index + 1}'
                                           value='${copyParty?.partyClass}'/>
                                    <input type='hidden' name='partyIdCopy' id='partyId-${index + 1}'
                                           value='${copyParty?.partyId}'/>
                                    <span class='delete-action'>
                                        <a style='cursor: pointer;'
                                           class='red icon-trash '
                                           onclick="deleteRow(${index+1});"
                                           title='<g:message code='default.button.delete.label'/>'>
                                        </a>
                                    </span>
                                </td>
                            </g:if>
                        </tr>
                    </rowElement>
                </g:each>
                <g:if test="${showActions && !copyToPartyList}">
                    <rowElement>
                        <tr id="row-0" class='center'>
                            <td class='center'></td>
                            <td class='center'></td>
                            <td class='center'></td>
                            <td class='center'></td>
                        </tr>
                    </rowElement>
                </g:if>
            </lay:table>
        </div>
    </el:formGroup>