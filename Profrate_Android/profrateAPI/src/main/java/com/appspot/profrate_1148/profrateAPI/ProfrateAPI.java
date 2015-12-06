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

package com.appspot.profrate_1148.profrateAPI;

/**
 * Service definition for ProfrateAPI (v1.0).
 *
 * <p>
 * This is an API
 * </p>
 *
 * <p>
 * For more information about this service, see the
 * <a href="" target="_blank">API Documentation</a>
 * </p>
 *
 * <p>
 * This service uses {@link ProfrateAPIRequestInitializer} to initialize global parameters via its
 * {@link Builder}.
 * </p>
 *
 * @since 1.3
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public class ProfrateAPI extends com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient {

  // Note: Leave this static initializer at the top of the file.
  static {
    com.google.api.client.util.Preconditions.checkState(
        com.google.api.client.googleapis.GoogleUtils.MAJOR_VERSION == 1 &&
        com.google.api.client.googleapis.GoogleUtils.MINOR_VERSION >= 15,
        "You are currently running with version %s of google-api-client. " +
        "You need at least version 1.15 of google-api-client to run version " +
        "1.21.0 of the profrateAPI library.", com.google.api.client.googleapis.GoogleUtils.VERSION);
  }

  /**
   * The default encoded root URL of the service. This is determined when the library is generated
   * and normally should not be changed.
   *
   * @since 1.7
   */
  public static final String DEFAULT_ROOT_URL = "https://profrate-1148.appspot.com/_ah/api/";

  /**
   * The default encoded service path of the service. This is determined when the library is
   * generated and normally should not be changed.
   *
   * @since 1.7
   */
  public static final String DEFAULT_SERVICE_PATH = "profrateAPI/v1.0/";

  /**
   * The default encoded base URL of the service. This is determined when the library is generated
   * and normally should not be changed.
   */
  public static final String DEFAULT_BASE_URL = DEFAULT_ROOT_URL + DEFAULT_SERVICE_PATH;

  /**
   * Constructor.
   *
   * <p>
   * Use {@link Builder} if you need to specify any of the optional parameters.
   * </p>
   *
   * @param transport HTTP transport, which should normally be:
   *        <ul>
   *        <li>Google App Engine:
   *        {@code com.google.api.client.extensions.appengine.http.UrlFetchTransport}</li>
   *        <li>Android: {@code newCompatibleTransport} from
   *        {@code com.google.api.client.extensions.android.http.AndroidHttp}</li>
   *        <li>Java: {@link com.google.api.client.googleapis.javanet.GoogleNetHttpTransport#newTrustedTransport()}
   *        </li>
   *        </ul>
   * @param jsonFactory JSON factory, which may be:
   *        <ul>
   *        <li>Jackson: {@code com.google.api.client.json.jackson2.JacksonFactory}</li>
   *        <li>Google GSON: {@code com.google.api.client.json.gson.GsonFactory}</li>
   *        <li>Android Honeycomb or higher:
   *        {@code com.google.api.client.extensions.android.json.AndroidJsonFactory}</li>
   *        </ul>
   * @param httpRequestInitializer HTTP request initializer or {@code null} for none
   * @since 1.7
   */
  public ProfrateAPI(com.google.api.client.http.HttpTransport transport, com.google.api.client.json.JsonFactory jsonFactory,
      com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
    this(new Builder(transport, jsonFactory, httpRequestInitializer));
  }

  /**
   * @param builder builder
   */
  ProfrateAPI(Builder builder) {
    super(builder);
  }

  @Override
  protected void initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest<?> httpClientRequest) throws java.io.IOException {
    super.initialize(httpClientRequest);
  }

  /**
   * Create a request for the method "professor_comment".
   *
   * This request holds the parameters needed by the profrateAPI server.  After setting any optional
   * parameters, call the {@link ProfessorComment#execute()} method to invoke the remote operation.
   *
   * @param content the {@link com.appspot.profrate_1148.profrateAPI.model.ProfrateProfessorCommentRequest}
   * @return the request
   */
  public ProfessorComment professorComment(com.appspot.profrate_1148.profrateAPI.model.ProfrateProfessorCommentRequest content) throws java.io.IOException {
    ProfessorComment result = new ProfessorComment(content);
    initialize(result);
    return result;
  }

  public class ProfessorComment extends ProfrateAPIRequest<com.appspot.profrate_1148.profrateAPI.model.SourceBackendAPIBooleanMessage> {

    private static final String REST_PATH = "professor_comment";

    /**
     * Create a request for the method "professor_comment".
     *
     * This request holds the parameters needed by the the profrateAPI server.  After setting any
     * optional parameters, call the {@link ProfessorComment#execute()} method to invoke the remote
     * operation. <p> {@link ProfessorComment#initialize(com.google.api.client.googleapis.services.Abs
     * tractGoogleClientRequest)} must be called to initialize this instance immediately after
     * invoking the constructor. </p>
     *
     * @param content the {@link com.appspot.profrate_1148.profrateAPI.model.ProfrateProfessorCommentRequest}
     * @since 1.13
     */
    protected ProfessorComment(com.appspot.profrate_1148.profrateAPI.model.ProfrateProfessorCommentRequest content) {
      super(ProfrateAPI.this, "POST", REST_PATH, content, com.appspot.profrate_1148.profrateAPI.model.SourceBackendAPIBooleanMessage.class);
    }

    @Override
    public ProfessorComment setAlt(java.lang.String alt) {
      return (ProfessorComment) super.setAlt(alt);
    }

    @Override
    public ProfessorComment setFields(java.lang.String fields) {
      return (ProfessorComment) super.setFields(fields);
    }

    @Override
    public ProfessorComment setKey(java.lang.String key) {
      return (ProfessorComment) super.setKey(key);
    }

    @Override
    public ProfessorComment setOauthToken(java.lang.String oauthToken) {
      return (ProfessorComment) super.setOauthToken(oauthToken);
    }

    @Override
    public ProfessorComment setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (ProfessorComment) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public ProfessorComment setQuotaUser(java.lang.String quotaUser) {
      return (ProfessorComment) super.setQuotaUser(quotaUser);
    }

    @Override
    public ProfessorComment setUserIp(java.lang.String userIp) {
      return (ProfessorComment) super.setUserIp(userIp);
    }

    @Override
    public ProfessorComment set(String parameterName, Object value) {
      return (ProfessorComment) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "professor_dislike".
   *
   * This request holds the parameters needed by the profrateAPI server.  After setting any optional
   * parameters, call the {@link ProfessorDislike#execute()} method to invoke the remote operation.
   *
   * @param content the {@link com.appspot.profrate_1148.profrateAPI.model.SourceBackendAPIIntegerMessage}
   * @return the request
   */
  public ProfessorDislike professorDislike(com.appspot.profrate_1148.profrateAPI.model.SourceBackendAPIIntegerMessage content) throws java.io.IOException {
    ProfessorDislike result = new ProfessorDislike(content);
    initialize(result);
    return result;
  }

  public class ProfessorDislike extends ProfrateAPIRequest<com.appspot.profrate_1148.profrateAPI.model.SourceBackendAPIBooleanMessage> {

    private static final String REST_PATH = "professor_dislike";

    /**
     * Create a request for the method "professor_dislike".
     *
     * This request holds the parameters needed by the the profrateAPI server.  After setting any
     * optional parameters, call the {@link ProfessorDislike#execute()} method to invoke the remote
     * operation. <p> {@link ProfessorDislike#initialize(com.google.api.client.googleapis.services.Abs
     * tractGoogleClientRequest)} must be called to initialize this instance immediately after
     * invoking the constructor. </p>
     *
     * @param content the {@link com.appspot.profrate_1148.profrateAPI.model.SourceBackendAPIIntegerMessage}
     * @since 1.13
     */
    protected ProfessorDislike(com.appspot.profrate_1148.profrateAPI.model.SourceBackendAPIIntegerMessage content) {
      super(ProfrateAPI.this, "POST", REST_PATH, content, com.appspot.profrate_1148.profrateAPI.model.SourceBackendAPIBooleanMessage.class);
    }

    @Override
    public ProfessorDislike setAlt(java.lang.String alt) {
      return (ProfessorDislike) super.setAlt(alt);
    }

    @Override
    public ProfessorDislike setFields(java.lang.String fields) {
      return (ProfessorDislike) super.setFields(fields);
    }

    @Override
    public ProfessorDislike setKey(java.lang.String key) {
      return (ProfessorDislike) super.setKey(key);
    }

    @Override
    public ProfessorDislike setOauthToken(java.lang.String oauthToken) {
      return (ProfessorDislike) super.setOauthToken(oauthToken);
    }

    @Override
    public ProfessorDislike setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (ProfessorDislike) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public ProfessorDislike setQuotaUser(java.lang.String quotaUser) {
      return (ProfessorDislike) super.setQuotaUser(quotaUser);
    }

    @Override
    public ProfessorDislike setUserIp(java.lang.String userIp) {
      return (ProfessorDislike) super.setUserIp(userIp);
    }

    @Override
    public ProfessorDislike set(String parameterName, Object value) {
      return (ProfessorDislike) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "professor_get".
   *
   * This request holds the parameters needed by the profrateAPI server.  After setting any optional
   * parameters, call the {@link ProfessorGet#execute()} method to invoke the remote operation.
   *
   * @param value
   * @return the request
   */
  public ProfessorGet professorGet(java.lang.Long value) throws java.io.IOException {
    ProfessorGet result = new ProfessorGet(value);
    initialize(result);
    return result;
  }

  public class ProfessorGet extends ProfrateAPIRequest<com.appspot.profrate_1148.profrateAPI.model.ProfrateProfessorResponse> {

    private static final String REST_PATH = "professor_get";

    /**
     * Create a request for the method "professor_get".
     *
     * This request holds the parameters needed by the the profrateAPI server.  After setting any
     * optional parameters, call the {@link ProfessorGet#execute()} method to invoke the remote
     * operation. <p> {@link
     * ProfessorGet#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
     * must be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @param value
     * @since 1.13
     */
    protected ProfessorGet(java.lang.Long value) {
      super(ProfrateAPI.this, "GET", REST_PATH, null, com.appspot.profrate_1148.profrateAPI.model.ProfrateProfessorResponse.class);
      this.value = com.google.api.client.util.Preconditions.checkNotNull(value, "Required parameter value must be specified.");
    }

    @Override
    public com.google.api.client.http.HttpResponse executeUsingHead() throws java.io.IOException {
      return super.executeUsingHead();
    }

    @Override
    public com.google.api.client.http.HttpRequest buildHttpRequestUsingHead() throws java.io.IOException {
      return super.buildHttpRequestUsingHead();
    }

    @Override
    public ProfessorGet setAlt(java.lang.String alt) {
      return (ProfessorGet) super.setAlt(alt);
    }

    @Override
    public ProfessorGet setFields(java.lang.String fields) {
      return (ProfessorGet) super.setFields(fields);
    }

    @Override
    public ProfessorGet setKey(java.lang.String key) {
      return (ProfessorGet) super.setKey(key);
    }

    @Override
    public ProfessorGet setOauthToken(java.lang.String oauthToken) {
      return (ProfessorGet) super.setOauthToken(oauthToken);
    }

    @Override
    public ProfessorGet setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (ProfessorGet) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public ProfessorGet setQuotaUser(java.lang.String quotaUser) {
      return (ProfessorGet) super.setQuotaUser(quotaUser);
    }

    @Override
    public ProfessorGet setUserIp(java.lang.String userIp) {
      return (ProfessorGet) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.Long value;

    /**

     */
    public java.lang.Long getValue() {
      return value;
    }

    public ProfessorGet setValue(java.lang.Long value) {
      this.value = value;
      return this;
    }

    @Override
    public ProfessorGet set(String parameterName, Object value) {
      return (ProfessorGet) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "professor_get_all".
   *
   * This request holds the parameters needed by the profrateAPI server.  After setting any optional
   * parameters, call the {@link ProfessorGetAll#execute()} method to invoke the remote operation.
   *
   * @return the request
   */
  public ProfessorGetAll professorGetAll() throws java.io.IOException {
    ProfessorGetAll result = new ProfessorGetAll();
    initialize(result);
    return result;
  }

  public class ProfessorGetAll extends ProfrateAPIRequest<com.appspot.profrate_1148.profrateAPI.model.ProfrateMultiProfessorResponse> {

    private static final String REST_PATH = "professor_get_all";

    /**
     * Create a request for the method "professor_get_all".
     *
     * This request holds the parameters needed by the the profrateAPI server.  After setting any
     * optional parameters, call the {@link ProfessorGetAll#execute()} method to invoke the remote
     * operation. <p> {@link ProfessorGetAll#initialize(com.google.api.client.googleapis.services.Abst
     * ractGoogleClientRequest)} must be called to initialize this instance immediately after invoking
     * the constructor. </p>
     *
     * @since 1.13
     */
    protected ProfessorGetAll() {
      super(ProfrateAPI.this, "GET", REST_PATH, null, com.appspot.profrate_1148.profrateAPI.model.ProfrateMultiProfessorResponse.class);
    }

    @Override
    public com.google.api.client.http.HttpResponse executeUsingHead() throws java.io.IOException {
      return super.executeUsingHead();
    }

    @Override
    public com.google.api.client.http.HttpRequest buildHttpRequestUsingHead() throws java.io.IOException {
      return super.buildHttpRequestUsingHead();
    }

    @Override
    public ProfessorGetAll setAlt(java.lang.String alt) {
      return (ProfessorGetAll) super.setAlt(alt);
    }

    @Override
    public ProfessorGetAll setFields(java.lang.String fields) {
      return (ProfessorGetAll) super.setFields(fields);
    }

    @Override
    public ProfessorGetAll setKey(java.lang.String key) {
      return (ProfessorGetAll) super.setKey(key);
    }

    @Override
    public ProfessorGetAll setOauthToken(java.lang.String oauthToken) {
      return (ProfessorGetAll) super.setOauthToken(oauthToken);
    }

    @Override
    public ProfessorGetAll setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (ProfessorGetAll) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public ProfessorGetAll setQuotaUser(java.lang.String quotaUser) {
      return (ProfessorGetAll) super.setQuotaUser(quotaUser);
    }

    @Override
    public ProfessorGetAll setUserIp(java.lang.String userIp) {
      return (ProfessorGetAll) super.setUserIp(userIp);
    }

    @Override
    public ProfessorGetAll set(String parameterName, Object value) {
      return (ProfessorGetAll) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "professor_get_articles".
   *
   * This request holds the parameters needed by the profrateAPI server.  After setting any optional
   * parameters, call the {@link ProfessorGetArticles#execute()} method to invoke the remote
   * operation.
   *
   * @param value
   * @return the request
   */
  public ProfessorGetArticles professorGetArticles(java.lang.Long value) throws java.io.IOException {
    ProfessorGetArticles result = new ProfessorGetArticles(value);
    initialize(result);
    return result;
  }

  public class ProfessorGetArticles extends ProfrateAPIRequest<com.appspot.profrate_1148.profrateAPI.model.ProfrateMultiArticleResponse> {

    private static final String REST_PATH = "professor_get_articles";

    /**
     * Create a request for the method "professor_get_articles".
     *
     * This request holds the parameters needed by the the profrateAPI server.  After setting any
     * optional parameters, call the {@link ProfessorGetArticles#execute()} method to invoke the
     * remote operation. <p> {@link ProfessorGetArticles#initialize(com.google.api.client.googleapis.s
     * ervices.AbstractGoogleClientRequest)} must be called to initialize this instance immediately
     * after invoking the constructor. </p>
     *
     * @param value
     * @since 1.13
     */
    protected ProfessorGetArticles(java.lang.Long value) {
      super(ProfrateAPI.this, "GET", REST_PATH, null, com.appspot.profrate_1148.profrateAPI.model.ProfrateMultiArticleResponse.class);
      this.value = com.google.api.client.util.Preconditions.checkNotNull(value, "Required parameter value must be specified.");
    }

    @Override
    public com.google.api.client.http.HttpResponse executeUsingHead() throws java.io.IOException {
      return super.executeUsingHead();
    }

    @Override
    public com.google.api.client.http.HttpRequest buildHttpRequestUsingHead() throws java.io.IOException {
      return super.buildHttpRequestUsingHead();
    }

    @Override
    public ProfessorGetArticles setAlt(java.lang.String alt) {
      return (ProfessorGetArticles) super.setAlt(alt);
    }

    @Override
    public ProfessorGetArticles setFields(java.lang.String fields) {
      return (ProfessorGetArticles) super.setFields(fields);
    }

    @Override
    public ProfessorGetArticles setKey(java.lang.String key) {
      return (ProfessorGetArticles) super.setKey(key);
    }

    @Override
    public ProfessorGetArticles setOauthToken(java.lang.String oauthToken) {
      return (ProfessorGetArticles) super.setOauthToken(oauthToken);
    }

    @Override
    public ProfessorGetArticles setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (ProfessorGetArticles) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public ProfessorGetArticles setQuotaUser(java.lang.String quotaUser) {
      return (ProfessorGetArticles) super.setQuotaUser(quotaUser);
    }

    @Override
    public ProfessorGetArticles setUserIp(java.lang.String userIp) {
      return (ProfessorGetArticles) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.Long value;

    /**

     */
    public java.lang.Long getValue() {
      return value;
    }

    public ProfessorGetArticles setValue(java.lang.Long value) {
      this.value = value;
      return this;
    }

    @Override
    public ProfessorGetArticles set(String parameterName, Object value) {
      return (ProfessorGetArticles) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "professor_get_comments".
   *
   * This request holds the parameters needed by the profrateAPI server.  After setting any optional
   * parameters, call the {@link ProfessorGetComments#execute()} method to invoke the remote
   * operation.
   *
   * @param value
   * @return the request
   */
  public ProfessorGetComments professorGetComments(java.lang.Long value) throws java.io.IOException {
    ProfessorGetComments result = new ProfessorGetComments(value);
    initialize(result);
    return result;
  }

  public class ProfessorGetComments extends ProfrateAPIRequest<com.appspot.profrate_1148.profrateAPI.model.ProfrateMultiCommentResponse> {

    private static final String REST_PATH = "professor_get_comments";

    /**
     * Create a request for the method "professor_get_comments".
     *
     * This request holds the parameters needed by the the profrateAPI server.  After setting any
     * optional parameters, call the {@link ProfessorGetComments#execute()} method to invoke the
     * remote operation. <p> {@link ProfessorGetComments#initialize(com.google.api.client.googleapis.s
     * ervices.AbstractGoogleClientRequest)} must be called to initialize this instance immediately
     * after invoking the constructor. </p>
     *
     * @param value
     * @since 1.13
     */
    protected ProfessorGetComments(java.lang.Long value) {
      super(ProfrateAPI.this, "GET", REST_PATH, null, com.appspot.profrate_1148.profrateAPI.model.ProfrateMultiCommentResponse.class);
      this.value = com.google.api.client.util.Preconditions.checkNotNull(value, "Required parameter value must be specified.");
    }

    @Override
    public com.google.api.client.http.HttpResponse executeUsingHead() throws java.io.IOException {
      return super.executeUsingHead();
    }

    @Override
    public com.google.api.client.http.HttpRequest buildHttpRequestUsingHead() throws java.io.IOException {
      return super.buildHttpRequestUsingHead();
    }

    @Override
    public ProfessorGetComments setAlt(java.lang.String alt) {
      return (ProfessorGetComments) super.setAlt(alt);
    }

    @Override
    public ProfessorGetComments setFields(java.lang.String fields) {
      return (ProfessorGetComments) super.setFields(fields);
    }

    @Override
    public ProfessorGetComments setKey(java.lang.String key) {
      return (ProfessorGetComments) super.setKey(key);
    }

    @Override
    public ProfessorGetComments setOauthToken(java.lang.String oauthToken) {
      return (ProfessorGetComments) super.setOauthToken(oauthToken);
    }

    @Override
    public ProfessorGetComments setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (ProfessorGetComments) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public ProfessorGetComments setQuotaUser(java.lang.String quotaUser) {
      return (ProfessorGetComments) super.setQuotaUser(quotaUser);
    }

    @Override
    public ProfessorGetComments setUserIp(java.lang.String userIp) {
      return (ProfessorGetComments) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.Long value;

    /**

     */
    public java.lang.Long getValue() {
      return value;
    }

    public ProfessorGetComments setValue(java.lang.Long value) {
      this.value = value;
      return this;
    }

    @Override
    public ProfessorGetComments set(String parameterName, Object value) {
      return (ProfessorGetComments) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "professor_get_rating".
   *
   * This request holds the parameters needed by the profrateAPI server.  After setting any optional
   * parameters, call the {@link ProfessorGetRating#execute()} method to invoke the remote operation.
   *
   * @param value
   * @return the request
   */
  public ProfessorGetRating professorGetRating(java.lang.Long value) throws java.io.IOException {
    ProfessorGetRating result = new ProfessorGetRating(value);
    initialize(result);
    return result;
  }

  public class ProfessorGetRating extends ProfrateAPIRequest<com.appspot.profrate_1148.profrateAPI.model.ProfrateRatingResponse> {

    private static final String REST_PATH = "professor_get_rating";

    /**
     * Create a request for the method "professor_get_rating".
     *
     * This request holds the parameters needed by the the profrateAPI server.  After setting any
     * optional parameters, call the {@link ProfessorGetRating#execute()} method to invoke the remote
     * operation. <p> {@link ProfessorGetRating#initialize(com.google.api.client.googleapis.services.A
     * bstractGoogleClientRequest)} must be called to initialize this instance immediately after
     * invoking the constructor. </p>
     *
     * @param value
     * @since 1.13
     */
    protected ProfessorGetRating(java.lang.Long value) {
      super(ProfrateAPI.this, "GET", REST_PATH, null, com.appspot.profrate_1148.profrateAPI.model.ProfrateRatingResponse.class);
      this.value = com.google.api.client.util.Preconditions.checkNotNull(value, "Required parameter value must be specified.");
    }

    @Override
    public com.google.api.client.http.HttpResponse executeUsingHead() throws java.io.IOException {
      return super.executeUsingHead();
    }

    @Override
    public com.google.api.client.http.HttpRequest buildHttpRequestUsingHead() throws java.io.IOException {
      return super.buildHttpRequestUsingHead();
    }

    @Override
    public ProfessorGetRating setAlt(java.lang.String alt) {
      return (ProfessorGetRating) super.setAlt(alt);
    }

    @Override
    public ProfessorGetRating setFields(java.lang.String fields) {
      return (ProfessorGetRating) super.setFields(fields);
    }

    @Override
    public ProfessorGetRating setKey(java.lang.String key) {
      return (ProfessorGetRating) super.setKey(key);
    }

    @Override
    public ProfessorGetRating setOauthToken(java.lang.String oauthToken) {
      return (ProfessorGetRating) super.setOauthToken(oauthToken);
    }

    @Override
    public ProfessorGetRating setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (ProfessorGetRating) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public ProfessorGetRating setQuotaUser(java.lang.String quotaUser) {
      return (ProfessorGetRating) super.setQuotaUser(quotaUser);
    }

    @Override
    public ProfessorGetRating setUserIp(java.lang.String userIp) {
      return (ProfessorGetRating) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.Long value;

    /**

     */
    public java.lang.Long getValue() {
      return value;
    }

    public ProfessorGetRating setValue(java.lang.Long value) {
      this.value = value;
      return this;
    }

    @Override
    public ProfessorGetRating set(String parameterName, Object value) {
      return (ProfessorGetRating) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "professor_like".
   *
   * This request holds the parameters needed by the profrateAPI server.  After setting any optional
   * parameters, call the {@link ProfessorLike#execute()} method to invoke the remote operation.
   *
   * @param content the {@link com.appspot.profrate_1148.profrateAPI.model.SourceBackendAPIIntegerMessage}
   * @return the request
   */
  public ProfessorLike professorLike(com.appspot.profrate_1148.profrateAPI.model.SourceBackendAPIIntegerMessage content) throws java.io.IOException {
    ProfessorLike result = new ProfessorLike(content);
    initialize(result);
    return result;
  }

  public class ProfessorLike extends ProfrateAPIRequest<com.appspot.profrate_1148.profrateAPI.model.SourceBackendAPIBooleanMessage> {

    private static final String REST_PATH = "professor_like";

    /**
     * Create a request for the method "professor_like".
     *
     * This request holds the parameters needed by the the profrateAPI server.  After setting any
     * optional parameters, call the {@link ProfessorLike#execute()} method to invoke the remote
     * operation. <p> {@link ProfessorLike#initialize(com.google.api.client.googleapis.services.Abstra
     * ctGoogleClientRequest)} must be called to initialize this instance immediately after invoking
     * the constructor. </p>
     *
     * @param content the {@link com.appspot.profrate_1148.profrateAPI.model.SourceBackendAPIIntegerMessage}
     * @since 1.13
     */
    protected ProfessorLike(com.appspot.profrate_1148.profrateAPI.model.SourceBackendAPIIntegerMessage content) {
      super(ProfrateAPI.this, "POST", REST_PATH, content, com.appspot.profrate_1148.profrateAPI.model.SourceBackendAPIBooleanMessage.class);
    }

    @Override
    public ProfessorLike setAlt(java.lang.String alt) {
      return (ProfessorLike) super.setAlt(alt);
    }

    @Override
    public ProfessorLike setFields(java.lang.String fields) {
      return (ProfessorLike) super.setFields(fields);
    }

    @Override
    public ProfessorLike setKey(java.lang.String key) {
      return (ProfessorLike) super.setKey(key);
    }

    @Override
    public ProfessorLike setOauthToken(java.lang.String oauthToken) {
      return (ProfessorLike) super.setOauthToken(oauthToken);
    }

    @Override
    public ProfessorLike setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (ProfessorLike) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public ProfessorLike setQuotaUser(java.lang.String quotaUser) {
      return (ProfessorLike) super.setQuotaUser(quotaUser);
    }

    @Override
    public ProfessorLike setUserIp(java.lang.String userIp) {
      return (ProfessorLike) super.setUserIp(userIp);
    }

    @Override
    public ProfessorLike set(String parameterName, Object value) {
      return (ProfessorLike) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "professor_rate".
   *
   * This request holds the parameters needed by the profrateAPI server.  After setting any optional
   * parameters, call the {@link ProfessorRate#execute()} method to invoke the remote operation.
   *
   * @param content the {@link com.appspot.profrate_1148.profrateAPI.model.ProfrateRateRequest}
   * @return the request
   */
  public ProfessorRate professorRate(com.appspot.profrate_1148.profrateAPI.model.ProfrateRateRequest content) throws java.io.IOException {
    ProfessorRate result = new ProfessorRate(content);
    initialize(result);
    return result;
  }

  public class ProfessorRate extends ProfrateAPIRequest<com.appspot.profrate_1148.profrateAPI.model.SourceBackendAPIBooleanMessage> {

    private static final String REST_PATH = "professor_rate";

    /**
     * Create a request for the method "professor_rate".
     *
     * This request holds the parameters needed by the the profrateAPI server.  After setting any
     * optional parameters, call the {@link ProfessorRate#execute()} method to invoke the remote
     * operation. <p> {@link ProfessorRate#initialize(com.google.api.client.googleapis.services.Abstra
     * ctGoogleClientRequest)} must be called to initialize this instance immediately after invoking
     * the constructor. </p>
     *
     * @param content the {@link com.appspot.profrate_1148.profrateAPI.model.ProfrateRateRequest}
     * @since 1.13
     */
    protected ProfessorRate(com.appspot.profrate_1148.profrateAPI.model.ProfrateRateRequest content) {
      super(ProfrateAPI.this, "POST", REST_PATH, content, com.appspot.profrate_1148.profrateAPI.model.SourceBackendAPIBooleanMessage.class);
    }

    @Override
    public ProfessorRate setAlt(java.lang.String alt) {
      return (ProfessorRate) super.setAlt(alt);
    }

    @Override
    public ProfessorRate setFields(java.lang.String fields) {
      return (ProfessorRate) super.setFields(fields);
    }

    @Override
    public ProfessorRate setKey(java.lang.String key) {
      return (ProfessorRate) super.setKey(key);
    }

    @Override
    public ProfessorRate setOauthToken(java.lang.String oauthToken) {
      return (ProfessorRate) super.setOauthToken(oauthToken);
    }

    @Override
    public ProfessorRate setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (ProfessorRate) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public ProfessorRate setQuotaUser(java.lang.String quotaUser) {
      return (ProfessorRate) super.setQuotaUser(quotaUser);
    }

    @Override
    public ProfessorRate setUserIp(java.lang.String userIp) {
      return (ProfessorRate) super.setUserIp(userIp);
    }

    @Override
    public ProfessorRate set(String parameterName, Object value) {
      return (ProfessorRate) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "professor_write_article".
   *
   * This request holds the parameters needed by the profrateAPI server.  After setting any optional
   * parameters, call the {@link ProfessorWriteArticle#execute()} method to invoke the remote
   * operation.
   *
   * @param content the {@link com.appspot.profrate_1148.profrateAPI.model.ProfrateWriteArtilceRequest}
   * @return the request
   */
  public ProfessorWriteArticle professorWriteArticle(com.appspot.profrate_1148.profrateAPI.model.ProfrateWriteArtilceRequest content) throws java.io.IOException {
    ProfessorWriteArticle result = new ProfessorWriteArticle(content);
    initialize(result);
    return result;
  }

  public class ProfessorWriteArticle extends ProfrateAPIRequest<com.appspot.profrate_1148.profrateAPI.model.SourceBackendAPIBooleanMessage> {

    private static final String REST_PATH = "professor_write_article";

    /**
     * Create a request for the method "professor_write_article".
     *
     * This request holds the parameters needed by the the profrateAPI server.  After setting any
     * optional parameters, call the {@link ProfessorWriteArticle#execute()} method to invoke the
     * remote operation. <p> {@link ProfessorWriteArticle#initialize(com.google.api.client.googleapis.
     * services.AbstractGoogleClientRequest)} must be called to initialize this instance immediately
     * after invoking the constructor. </p>
     *
     * @param content the {@link com.appspot.profrate_1148.profrateAPI.model.ProfrateWriteArtilceRequest}
     * @since 1.13
     */
    protected ProfessorWriteArticle(com.appspot.profrate_1148.profrateAPI.model.ProfrateWriteArtilceRequest content) {
      super(ProfrateAPI.this, "POST", REST_PATH, content, com.appspot.profrate_1148.profrateAPI.model.SourceBackendAPIBooleanMessage.class);
    }

    @Override
    public ProfessorWriteArticle setAlt(java.lang.String alt) {
      return (ProfessorWriteArticle) super.setAlt(alt);
    }

    @Override
    public ProfessorWriteArticle setFields(java.lang.String fields) {
      return (ProfessorWriteArticle) super.setFields(fields);
    }

    @Override
    public ProfessorWriteArticle setKey(java.lang.String key) {
      return (ProfessorWriteArticle) super.setKey(key);
    }

    @Override
    public ProfessorWriteArticle setOauthToken(java.lang.String oauthToken) {
      return (ProfessorWriteArticle) super.setOauthToken(oauthToken);
    }

    @Override
    public ProfessorWriteArticle setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (ProfessorWriteArticle) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public ProfessorWriteArticle setQuotaUser(java.lang.String quotaUser) {
      return (ProfessorWriteArticle) super.setQuotaUser(quotaUser);
    }

    @Override
    public ProfessorWriteArticle setUserIp(java.lang.String userIp) {
      return (ProfessorWriteArticle) super.setUserIp(userIp);
    }

    @Override
    public ProfessorWriteArticle set(String parameterName, Object value) {
      return (ProfessorWriteArticle) super.set(parameterName, value);
    }
  }

  /**
   * Builder for {@link ProfrateAPI}.
   *
   * <p>
   * Implementation is not thread-safe.
   * </p>
   *
   * @since 1.3.0
   */
  public static final class Builder extends com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient.Builder {

    /**
     * Returns an instance of a new builder.
     *
     * @param transport HTTP transport, which should normally be:
     *        <ul>
     *        <li>Google App Engine:
     *        {@code com.google.api.client.extensions.appengine.http.UrlFetchTransport}</li>
     *        <li>Android: {@code newCompatibleTransport} from
     *        {@code com.google.api.client.extensions.android.http.AndroidHttp}</li>
     *        <li>Java: {@link com.google.api.client.googleapis.javanet.GoogleNetHttpTransport#newTrustedTransport()}
     *        </li>
     *        </ul>
     * @param jsonFactory JSON factory, which may be:
     *        <ul>
     *        <li>Jackson: {@code com.google.api.client.json.jackson2.JacksonFactory}</li>
     *        <li>Google GSON: {@code com.google.api.client.json.gson.GsonFactory}</li>
     *        <li>Android Honeycomb or higher:
     *        {@code com.google.api.client.extensions.android.json.AndroidJsonFactory}</li>
     *        </ul>
     * @param httpRequestInitializer HTTP request initializer or {@code null} for none
     * @since 1.7
     */
    public Builder(com.google.api.client.http.HttpTransport transport, com.google.api.client.json.JsonFactory jsonFactory,
        com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
      super(
          transport,
          jsonFactory,
          DEFAULT_ROOT_URL,
          DEFAULT_SERVICE_PATH,
          httpRequestInitializer,
          false);
    }

    /** Builds a new instance of {@link ProfrateAPI}. */
    @Override
    public ProfrateAPI build() {
      return new ProfrateAPI(this);
    }

    @Override
    public Builder setRootUrl(String rootUrl) {
      return (Builder) super.setRootUrl(rootUrl);
    }

    @Override
    public Builder setServicePath(String servicePath) {
      return (Builder) super.setServicePath(servicePath);
    }

    @Override
    public Builder setHttpRequestInitializer(com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
      return (Builder) super.setHttpRequestInitializer(httpRequestInitializer);
    }

    @Override
    public Builder setApplicationName(String applicationName) {
      return (Builder) super.setApplicationName(applicationName);
    }

    @Override
    public Builder setSuppressPatternChecks(boolean suppressPatternChecks) {
      return (Builder) super.setSuppressPatternChecks(suppressPatternChecks);
    }

    @Override
    public Builder setSuppressRequiredParameterChecks(boolean suppressRequiredParameterChecks) {
      return (Builder) super.setSuppressRequiredParameterChecks(suppressRequiredParameterChecks);
    }

    @Override
    public Builder setSuppressAllChecks(boolean suppressAllChecks) {
      return (Builder) super.setSuppressAllChecks(suppressAllChecks);
    }

    /**
     * Set the {@link ProfrateAPIRequestInitializer}.
     *
     * @since 1.12
     */
    public Builder setProfrateAPIRequestInitializer(
        ProfrateAPIRequestInitializer profrateapiRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(profrateapiRequestInitializer);
    }

    @Override
    public Builder setGoogleClientRequestInitializer(
        com.google.api.client.googleapis.services.GoogleClientRequestInitializer googleClientRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(googleClientRequestInitializer);
    }
  }
}
