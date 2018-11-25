<lay:widget transparent="true" color="blue" icon="icon-user" title="${g.message(code: "employee.info.label")}">
    <lay:widgetBody>

        <lay:showWidget size="6">

            <lay:showElement
                    value="${employee?.toString()}"
                    type="String"
                    label="${message(code: 'employee.label', default: 'personName')}"/>



            <lay:showElement value="${employee?.transientData?.governorateDTO?.descriptionInfo}" type="String"
                             label="${message(code: 'department.governorateName.label', default: 'governorateName')}"/>



            <lay:showElement value="${employee?.employmentDate}" type="ZonedDate"
                             label="${message(code: 'employee.employmentDate.label', default: 'employmentDate')}"/>


            <lay:showElement value="${employee?.militaryNumber}" type="String"
                             label="${message(code: 'employee.militaryNumber.label', default: 'militaryNumber')}"/>

            %{--in case: render employee wrapper in vacation forms--}%
            <g:if test="${isVacation}">
                <lay:showElement value="${vacationRequest?.vacationType?.descriptionInfo?.localName}" type="string"
                                 label="${message(code: 'vacationRequest.vacationType.label', default: 'vacationType')}"/>
            </g:if>
            %{--in case: render employee wrapper in marital status request or child request--}%
            <g:if test="${isMSRequest}">
                    <lay:showElement value="${employee?.transientData?.personDTO?.recentCardNo}" type="String"
                                     label="${message(code: 'person.recentCardNo.label', default: 'recentCardNo')}"/>
            </g:if>



        </lay:showWidget>

        <lay:showWidget size="6">

            <lay:showElement value="${employee?.currentEmploymentRecord?.jobTitle?.descriptionInfo}" type="String"
                             label="${message(code: 'employee.jobTitle.label', default: 'jobTitle')}"/>



            <lay:showElement value="${employee?.currentEmploymentRecord?.department?.descriptionInfo}" type="String"
                             label="${message(code: 'employee.department.label', default: 'department')}"/>



            <lay:showElement value="${employee?.id}" type="String"
                             label="${message(code: 'employee.id.label', default: 'id')}"/>

            <lay:showElement value="${employee?.financialNumber}" type="String"
                             label="${message(code: 'employee.financialNumber.label', default: 'financialNumber')}"/>

            %{--in case: render employee wrapper in promotion forms--}%
            <g:if test="${isPromotion}">
                <lay:showElement
                        value="${employee?.currentEmployeeMilitaryRank?.actualDueDate}"
                        type="ZonedDate"
                        label="${message(code: 'employee.employeePromotion.actualDueDate.label', default: 'actualDueDate')}"/>
            </g:if>

            %{--in case: render employee wrapper in vacation forms--}%
            <g:if test="${isVacation}">
                <lay:showElement
                        value="${vacationRequest?.currentBalance > 0 ? vacationRequest?.currentBalance : "0"}"
                        type="short"
                        label="${message(code: 'vacationRequest.currentBalance.label', default: 'currentBalance')}"/>
            </g:if>

            %{--in case: render employee wrapper in marital status request or child request--}%
            <g:if test="${isMSRequest}">
                    <lay:showElement value="${employee?.transientData?.personDTO?.genderType?.descriptionInfo?.localName}"
                                     type="String"
                                     label="${message(code: 'person.genderType.label', default: 'genderType')}"/>
            </g:if>



        %{--in case: render employee wrapper in allowance forms--}%
            <g:if test="${isAllowance}">
                <lay:showElement value="${allowanceRequest?.allowanceType?.descriptionInfo?.localName}" type="string"
                                 label="${message(code: 'allowanceRequest.allowanceType.label', default: 'allowanceType')}"/>
            </g:if>


        </lay:showWidget>


        <el:row/>
    </lay:widgetBody>
</lay:widget>
