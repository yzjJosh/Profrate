/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://github.com/google/apis-client-generator/
 * (build: 2015-11-16 19:10:01 UTC)
 * on 2015-12-06 at 02:28:43 UTC 
 * Modify at your own risk.
 */

package com.appspot.profrate_1148.profrateAPI.model;

/**
 * Model definition for ProfrateProfessorResponse.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the profrateAPI. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class ProfrateProfessorResponse extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private ProfrateProfessorMessage professor;

  /**
   * @return value or {@code null} for none
   */
  public ProfrateProfessorMessage getProfessor() {
    return professor;
  }

  /**
   * @param professor professor or {@code null} for none
   */
  public ProfrateProfessorResponse setProfessor(ProfrateProfessorMessage professor) {
    this.professor = professor;
    return this;
  }

  @Override
  public ProfrateProfessorResponse set(String fieldName, Object value) {
    return (ProfrateProfessorResponse) super.set(fieldName, value);
  }

  @Override
  public ProfrateProfessorResponse clone() {
    return (ProfrateProfessorResponse) super.clone();
  }

}
