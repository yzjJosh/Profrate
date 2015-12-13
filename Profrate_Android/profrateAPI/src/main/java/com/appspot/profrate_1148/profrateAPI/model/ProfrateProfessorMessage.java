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
 * on 2015-12-13 at 08:04:10 UTC 
 * Modify at your own risk.
 */

package com.appspot.profrate_1148.profrateAPI.model;

/**
 * Model definition for ProfrateProfessorMessage.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the profrateAPI. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class ProfrateProfessorMessage extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("article_num") @com.google.api.client.json.JsonString
  private java.lang.Long articleNum;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("comment_num") @com.google.api.client.json.JsonString
  private java.lang.Long commentNum;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("disliked_by")
  private java.util.List<java.lang.String> dislikedBy;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String email;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String image;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String introduction;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("liked_by")
  private java.util.List<java.lang.String> likedBy;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String name;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String office;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("overall_rating")
  private ProfrateRatingMessage overallRating;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("personal_website")
  private java.lang.String personalWebsite;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String phone;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("research_areas")
  private java.util.List<java.lang.String> researchAreas;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("research_groups")
  private java.util.List<java.lang.String> researchGroups;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("research_interests")
  private java.util.List<java.lang.String> researchInterests;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("special_title")
  private java.lang.String specialTitle;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String title;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getArticleNum() {
    return articleNum;
  }

  /**
   * @param articleNum articleNum or {@code null} for none
   */
  public ProfrateProfessorMessage setArticleNum(java.lang.Long articleNum) {
    this.articleNum = articleNum;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getCommentNum() {
    return commentNum;
  }

  /**
   * @param commentNum commentNum or {@code null} for none
   */
  public ProfrateProfessorMessage setCommentNum(java.lang.Long commentNum) {
    this.commentNum = commentNum;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<java.lang.String> getDislikedBy() {
    return dislikedBy;
  }

  /**
   * @param dislikedBy dislikedBy or {@code null} for none
   */
  public ProfrateProfessorMessage setDislikedBy(java.util.List<java.lang.String> dislikedBy) {
    this.dislikedBy = dislikedBy;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getEmail() {
    return email;
  }

  /**
   * @param email email or {@code null} for none
   */
  public ProfrateProfessorMessage setEmail(java.lang.String email) {
    this.email = email;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getId() {
    return id;
  }

  /**
   * @param id id or {@code null} for none
   */
  public ProfrateProfessorMessage setId(java.lang.Long id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getImage() {
    return image;
  }

  /**
   * @param image image or {@code null} for none
   */
  public ProfrateProfessorMessage setImage(java.lang.String image) {
    this.image = image;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getIntroduction() {
    return introduction;
  }

  /**
   * @param introduction introduction or {@code null} for none
   */
  public ProfrateProfessorMessage setIntroduction(java.lang.String introduction) {
    this.introduction = introduction;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<java.lang.String> getLikedBy() {
    return likedBy;
  }

  /**
   * @param likedBy likedBy or {@code null} for none
   */
  public ProfrateProfessorMessage setLikedBy(java.util.List<java.lang.String> likedBy) {
    this.likedBy = likedBy;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getName() {
    return name;
  }

  /**
   * @param name name or {@code null} for none
   */
  public ProfrateProfessorMessage setName(java.lang.String name) {
    this.name = name;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getOffice() {
    return office;
  }

  /**
   * @param office office or {@code null} for none
   */
  public ProfrateProfessorMessage setOffice(java.lang.String office) {
    this.office = office;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public ProfrateRatingMessage getOverallRating() {
    return overallRating;
  }

  /**
   * @param overallRating overallRating or {@code null} for none
   */
  public ProfrateProfessorMessage setOverallRating(ProfrateRatingMessage overallRating) {
    this.overallRating = overallRating;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getPersonalWebsite() {
    return personalWebsite;
  }

  /**
   * @param personalWebsite personalWebsite or {@code null} for none
   */
  public ProfrateProfessorMessage setPersonalWebsite(java.lang.String personalWebsite) {
    this.personalWebsite = personalWebsite;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getPhone() {
    return phone;
  }

  /**
   * @param phone phone or {@code null} for none
   */
  public ProfrateProfessorMessage setPhone(java.lang.String phone) {
    this.phone = phone;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<java.lang.String> getResearchAreas() {
    return researchAreas;
  }

  /**
   * @param researchAreas researchAreas or {@code null} for none
   */
  public ProfrateProfessorMessage setResearchAreas(java.util.List<java.lang.String> researchAreas) {
    this.researchAreas = researchAreas;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<java.lang.String> getResearchGroups() {
    return researchGroups;
  }

  /**
   * @param researchGroups researchGroups or {@code null} for none
   */
  public ProfrateProfessorMessage setResearchGroups(java.util.List<java.lang.String> researchGroups) {
    this.researchGroups = researchGroups;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<java.lang.String> getResearchInterests() {
    return researchInterests;
  }

  /**
   * @param researchInterests researchInterests or {@code null} for none
   */
  public ProfrateProfessorMessage setResearchInterests(java.util.List<java.lang.String> researchInterests) {
    this.researchInterests = researchInterests;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getSpecialTitle() {
    return specialTitle;
  }

  /**
   * @param specialTitle specialTitle or {@code null} for none
   */
  public ProfrateProfessorMessage setSpecialTitle(java.lang.String specialTitle) {
    this.specialTitle = specialTitle;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getTitle() {
    return title;
  }

  /**
   * @param title title or {@code null} for none
   */
  public ProfrateProfessorMessage setTitle(java.lang.String title) {
    this.title = title;
    return this;
  }

  @Override
  public ProfrateProfessorMessage set(String fieldName, Object value) {
    return (ProfrateProfessorMessage) super.set(fieldName, value);
  }

  @Override
  public ProfrateProfessorMessage clone() {
    return (ProfrateProfessorMessage) super.clone();
  }

}
