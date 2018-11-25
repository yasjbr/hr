<div class="col-md-12">
    <div class="tabbable">

        <lay:widget color="green" icon="icon-home" title="${g.message(code: "person.localName.label")}">
            <lay:widgetBody>
                <el:formGroup>

                    <el:textField type="text" name="initials" size="4" class=""
                                  label="${message(code: 'person.initials.label', default: 'initials')}"
                                  value="${person?.initials}"/>



                    <el:textField type="text" name="localFirstName" size="4" class=" isRequired"
                                  label="${message(code: 'person.localFirstName.label', default: 'localFirstName')}"
                                  value="${person?.localFirstName}"/>

                    <el:textField type="text" name="localSecondName" size="4" class=""
                                  label="${message(code: 'person.localSecondName.label', default: 'localSecondName')}"
                                  value="${person?.localSecondName}"/>
                </el:formGroup>
                <el:formGroup>
                    <el:textField type="text" name="localThirdName" size="4" class=""
                                  label="${message(code: 'person.localThirdName.label', default: 'localThirdName')}"
                                  value="${person?.localThirdName}"/>



                    <el:textField type="text" name="localFourthName" size="4" class=" isRequired"
                                  label="${message(code: 'person.localFourthName.label', default: 'localFourthName')}"
                                  value="${person?.localFourthName}"/>

                    <el:textField type="text" name="localMotherName" size="4" class=""
                                  label="${message(code: 'person.localMotherName.label', default: 'localMotherName')}"
                                  value="${person?.localMotherName}"/>

                </el:formGroup>

                <el:formGroup>
                    <el:textField type="text" name="localOldName" size="4" class=""
                                  label="${message(code: 'person.localOldName.label', default: 'localOldName')}"
                                  value="${person?.localOldName}"/>
                </el:formGroup>
            </lay:widgetBody>
        </lay:widget>


        <lay:collapseWidget collapsed="false" color="green" id="latinName" icon="icon-globe"
                            title="${g.message(code: "person.latinName.label")}" size="12">
            <lay:widgetBody>

                <el:formGroup>

                    <el:textField type="text" name="latinFirstName" size="4" class=""
                                  label="${message(code: 'person.latinFirstName.label', default: 'latinFirstName')}"
                                  value="${person?.latinFirstName}"/>

                    <el:textField type="text" name="latinSecondName" size="4" class=""
                                  label="${message(code: 'person.latinSecondName.label', default: 'latinSecondName')}"
                                  value="${person?.latinSecondName}"/>


                    <el:textField type="text" name="latinThirdName" size="4" class=""
                                  label="${message(code: 'person.latinThirdName.label', default: 'latinThirdName')}"
                                  value="${person?.latinThirdName}"/>

                </el:formGroup>

                <el:formGroup>

                    <el:textField type="text" name="latinFourthName" size="4" class=""
                                  label="${message(code: 'person.latinFourthName.label', default: 'latinFourthName')}"
                                  value="${person?.latinFourthName}"/>


                    <el:textField type="text" name="latinMotherName" size="4" class=""
                                  label="${message(code: 'person.latinMotherName.label', default: 'latinMotherName')}"
                                  value="${person?.latinMotherName}"/>


                    <el:textField type="text" name="latinOldName" size="4" class=""
                                  label="${message(code: 'person.latinOldName.label', default: 'latinOldName')}"
                                  value="${person?.latinOldName}"/>

                </el:formGroup>
            </lay:widgetBody>
        </lay:collapseWidget>

        <lay:collapseWidget collapsed="false" color="green" id="hebrowName" icon="icon-language"
                            title="${g.message(code: "person.hebrowName.label")}" size="12">
            <lay:widgetBody>

                <el:formGroup>

                    <el:textField type="text" name="hebrowFirstName" size="4" class=""
                                  label="${message(code: 'person.hebrowFirstName.label', default: 'hebrowFirstName')}"
                                  value="${person?.hebrowFirstName}"/>

                    <el:textField type="text" name="hebrowSecondName" size="4" class=""
                                  label="${message(code: 'person.hebrowSecondName.label', default: 'hebrowSecondName')}"
                                  value="${person?.hebrowSecondName}"/>


                    <el:textField type="text" name="hebrowThirdName" size="4" class=""
                                  label="${message(code: 'person.hebrowThirdName.label', default: 'hebrowThirdName')}"
                                  value="${person?.hebrowThirdName}"/>

                </el:formGroup>


                <el:formGroup>

                    <el:textField type="text" name="hebrowFourthName" size="4" class=""
                                  label="${message(code: 'person.hebrowFourthName.label', default: 'hebrowFourthName')}"
                                  value="${person?.hebrowFourthName}"/>



                    <el:textField type="text" name="hebrowMotherName" size="4" class=""
                                  label="${message(code: 'person.hebrowMotherName.label', default: 'hebrowMotherName')}"
                                  value="${person?.hebrowMotherName}"/>



                    <el:textField type="text" name="hebrowOldName" size="4" class=""
                                  label="${message(code: 'person.hebrowOldName.label', default: 'hebrowOldName')}"
                                  value="${person?.hebrowOldName}"/>

                </el:formGroup>
            </lay:widgetBody>
        </lay:collapseWidget>
    </div>

</div>

<div class="col-md-12">
    <div class="tabbable">

        <lay:widget icon="icon-vcard-1" color="blue" class="col-md-12" title="${g.message(code: "person.info.label")}">
            <lay:widgetBody>
                <el:formGroup>

                    <msg:warning label="${message(code: 'person.legalIdentifierInfo.label')}"/>

                    <el:dateField zoned="true" name="dateOfBirth" size="6" class=" isRequired"
                                  label="${message(code: 'person.dateOfBirth.label', default: 'dateOfBirth')}"
                                  value="${person?.dateOfBirth}" isMaxDate="true"/>

                    <el:autocomplete optionKey="id" optionValue="name" size="6"
                                     class="" controller="pcore" action="bloodTypeAutoComplete"
                                     name="bloodType.id"
                                     label="${message(code: 'person.bloodType.label', default: 'bloodType')}"
                                     values="${[[person?.bloodType?.id, person?.bloodType?.descriptionInfo?.localName]]}"/>
                </el:formGroup>
                <el:formGroup>

                    <el:idField name="recentCardNo" size="6" class=""
                                  label="${message(code: 'person.recentCardNo.label', default: 'recentCardNo')}"
                                  value="${person?.recentCardNo}"/>



                    <el:textField type="text" name="recentPassportNo" size="6" class=""
                                  label="${message(code: 'person.recentPassportNo.label', default: 'recentPassportNo')}"
                                  value="${person?.recentPassportNo}"/>

                </el:formGroup>

                <el:formGroup>

                    <el:autocomplete optionKey="id" optionValue="name" size="6"
                                     class="" controller="pcore" action="genderTypeAutoComplete"
                                     name="genderType.id"
                                     label="${message(code: 'person.genderType.label', default: 'genderType')}"
                                     values="${[[person?.genderType?.id, person?.genderType?.descriptionInfo?.localName]]}"/>

                    <el:autocomplete optionKey="id" optionValue="name" size="6"
                                     class="" controller="pcore" action="religionAutoComplete"
                                     name="religion.id"
                                     label="${message(code: 'person.religion.label', default: 'religion')}"
                                     values="${[[person?.religion?.id, person?.religion?.descriptionInfo?.localName]]}"/>
                </el:formGroup>

                <el:formGroup>

                    <el:textField type="text" name="personNickname" size="6" class=""
                                  label="${message(code: 'person.personNickname.label', default: 'personNickname')}"
                                  value="${person?.personNickname}"/>

                    <el:autocomplete optionKey="id" optionValue="name" size="6" class=""
                                     controller="pcore" action="ethnicityAutoComplete" name="ethnicity.id"
                                     label="${message(code: 'person.ethnicity.label', default: 'ethnicity')}"
                                     values="${[[person?.ethnicity?.id, person?.ethnicity?.descriptionInfo?.localName]]}"/>

                </el:formGroup>

                <el:formGroup>

                    <el:autocomplete optionKey="id" optionValue="name" multiple="true" size="6" class=""
                                     controller="pcore" action="competencyAutoComplete" name="competency.id"
                                     label="${message(code: 'person.personCompetencies.label', default: 'personCompetencies')}"
                                     values="${person?.personCompetencies?.competency?.collect {
                                         return [it?.id, it?.descriptionInfo?.localName]
                                     }}"/>

                    <el:checkboxField name="needRevision" size="6" class=""
                                      label="${message(code: 'person.needRevision.label', default: 'needRevision')}"
                                      value="${person?.needRevision}" isChecked="${person?.needRevision}"/>

                </el:formGroup>


                <el:formGroup>
                    <el:autocomplete optionKey="id" optionValue="name" size="6"
                                     class=" isRequired"
                                     controller="maritalStatus"
                                     action="autocomplete"
                                     name="maritalStatus.id"
                                     label="${message(code: 'person.personMaritalStatus.label', default: 'maritalStatus')}"
                                     values="${[[person?.personMaritalStatuses?.find {
                                         it.isCurrent == true
                                     }?.id, person?.personMaritalStatuses?.find {
                                         it.isCurrent == true
                                     }?.maritalStatus?.descriptionInfo?.localName]]}"/>
                    <el:dateField
                            label="${message(code: 'person.personMaritalStatus.fromDate.label', default: 'fromDate')}"
                            name="fromDate" size="6"/>
                </el:formGroup>

                <lay:widget icon="icon-location" color="blue" class="col-md-12"
                            title="${g.message(code: "person.birthPlace.label")}">
                    <lay:widgetBody>
                        <g:render template="/pcore/location/staticWrapper"
                                  model="[location         : person?.birthPlace,
                                          isCountryRequired: true,
                                          hiddenDetails    : true,
                                  ]"/>
                        <el:formGroup>
                            <el:textArea name="unstructuredBirthPlaceLocation" size="6" class=" "
                                         label="${message(code: 'person.unstructuredBirthPlaceLocation.label', default: 'unstructuredBirthPlaceLocation')}"
                                         value="${person?.unstructuredBirthPlaceLocation}"/>
                        </el:formGroup>
                    </lay:widgetBody>
                </lay:widget>
            </lay:widgetBody>
        </lay:widget>
    </div>
</div>