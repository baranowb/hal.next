/*
 * Copyright 2015-2016 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jboss.hal.dmr;

/**
 * String constants frequently used in model descriptions and DMR operations.
 *
 * @author Brian Stansberry
 * @author Harald Pehl
 */
public interface ModelDescriptionConstants {

    // KEEP THESE IN ALPHABETICAL ORDER!
    String ACCESS_CONTROL = "access-control";
    String ACCESS_TYPE = "access-type";
    String ADD = "add";
    String ADDRESS = "address";
    String ALLOWED = "allowed";
    String ANY = "any";
    String ATTRIBUTES = "attributes";
    String ATTRIBUTES_ONLY = "attributes-only";
    String AUTO_START = "auto-start";

    String BASE_ROLE = "base-role";
    String BATCH_JBERET = "batch-jberet";
    String BLOCKING = "blocking";

    String CAPABILITIES = "capabilities";
    String CAPABILITY_REFERENCE = "capability-reference";
    String CAPABILITY_REGISTRY = "capability-registry";
    String CHILD_TYPE = "child-type";
    String CHILDREN = "children";
    String CLASS_NAME = "class-name";
    String COMBINED_DESCRIPTIONS = "combined-descriptions";
    String COMPOSITE = "composite";
    String CONNECTION_URL = "connection-url";
    String CONFIGURATION = "configuration";
    String CONTEXT_SERVICE = "context-service";
    String CORE_SERVICE = "core-service";

    String DATA_SOURCE = "data-source";
    String DATASOURCES = "datasources";
    String DEFAULT = "default";
    String DEFAULT_BINDINGS = "default-bindings";
    String DEPLOYMENT = "deployment";
    String DEPLOYMENT_NAME = "deployment-name";
    String DEPLOYMENT_SCANNER = "deployment-scanner";
    String DEPRECATED = "deprecated";
    String DESCRIPTION = "description";
    String DISABLED = "disabled";
    String DRIVER_CLASS = "driver-class";
    String DRIVER_CLASS_NAME = "driver-class-name";
    String DRIVER_DATASOURCE_CLASS_NAME = "driver-datasource-class-name";
    String DRIVER_MODULE_NAME = "driver-module-name";
    String DRIVER_NAME = "driver-name";
    String DRIVER_MAJOR_VERSION = "driver-major-version";
    String DRIVER_MINOR_VERSION = "driver-minor-version";
    String DRIVER_VERSION = "driver-version";
    String DRIVER_XA_DATASOURCE_CLASS_NAME = "driver-xa-datasource-class-name";

    String EE = "ee";
    String EJB3 = "ejb3";
    String ENABLED = "enabled";
    String EXCEPTIONS = "exceptions";
    String EXCLUDE = "exclude";
    String EXPRESSIONS_ALLOWED = "expressions-allowed";

    String FAILED = "failed";
    String FAILURE_DESCRIPTION = "failure-description";
    String FILE = "file";
    String FULL_REPLACE_DEPLOYMENT = "full-replace-deployment";

    String GLOBAL_MODULES = "global-modules";
    String GROUP = "group";

    String HANDLERS = "handlers";
    String HOST = "host";
    String HOSTS = "hosts";
    String HOST_SCOPED_ROLE = "host-scoped-role";
    String HOST_STATE = "host-state";

    String IIOP_OPENJDK = "iiop-openjdk";
    String IMAP = "imap";
    String INCLUDE = "include";
    String INCLUDE_ALIASES = "include-aliases";
    String INCLUDE_ALL = "include-all";
    String INCLUDE_DEFAULTS = "include-defaults";
    String INCLUDE_RUNTIME = "include-runtime";
    String INCLUDE_SINGLETONS = "include-singletons";
    String INFINISPAN = "infinispan";
    String INHERITED = "inherited";
    String INTERFACE = "interface";
    String IO = "io";

    String JCA = "jca";
    String JDBC_DRIVER = "jdbc-driver";
    String JMX = "jmx";
    String JNDI_NAME = "jndi-name";
    String JPA = "jpa";
    String JVM = "jvm";

    String LAUNCH_TYPE = "launch-type";
    String LEVEL = "level";
    String LIST_ADD = "list-add";
    String LIST_REMOVE = "list-remove";
    String LOCALE = "locale";
    String LOGGING = "logging";
    String LOGGING_PROFILE = "logging-profile";

    String MAIL = "mail";
    String MAIL_SESSION = "mail-session";
    String MANAGED_EXECUTOR_SERVICE = "managed-executor-service";
    String MANAGED_SCHEDULED_EXECUTOR_SERVICE = "managed-scheduled-executor-service";
    String MANAGED_THREAD_FACTORY = "managed-thread-factory";
    String MANAGEMENT_MAJOR_VERSION = "management-major-version";
    String MANAGEMENT_MICRO_VERSION = "management-micro-version";
    String MANAGEMENT_MINOR_VERSION = "management-minor-version";
    String MAX = "max";
    String MESSAGING_ACTIVEMQ = "messaging-activemq";
    String METRIC = "metric";
    String MIN = "min";
    String MIN_VALUE = "min-value";
    String MODEL_DESCRIPTION = "model-description";

    String NAME = "name";
    String NILLABLE = "nillable";
    String NOT = "not";

    String OP = "operation";
    String OPERATIONS = "operations";
    String OPERATION_HEADERS = "operation-headers";
    String OUTBOUND_SOCKET_BINDING_REF = "outbound-socket-binding-ref";
    String OUTCOME = "outcome";

    String PASSWORD = "password";
    String PATH = "path";
    String POP3 = "pop3";
    String PORT = "port";
    String PROCESS_STATE = "process-state";
    String PRODUCT_NAME = "product-name";
    String PRODUCT_VERSION = "product-version";
    String PROFILE = "profile";
    String PROFILE_NAME = "profile-name";
    String PROVIDER = "provider";
    String PROXIES = "proxies";

    String QUERY = "query";

    String READ_ATTRIBUTE_OPERATION = "read-attribute";
    String READ_CHILDREN_NAMES_OPERATION = "read-children-names";
    String READ_CHILDREN_TYPES_OPERATION = "read-children-types";
    String READ_CHILDREN_RESOURCES_OPERATION = "read-children-resources";
    String READ_ONLY = "read-only";
    String READ_RESOURCE_DESCRIPTION_OPERATION = "read-resource-description";
    String READ_RESOURCE_OPERATION = "read-resource";
    String READ_WRITE = "read-write";
    String REALM = "realm";
    String REASON = "reason";
    String RELATIVE_TO = "relative-to";
    String REMOTING = "remoting";
    String REMOVE = "remove";
    String REQUEST_PROPERTIES = "request-properties";
    String RECURSIVE = "recursive";
    String RECURSIVE_DEPTH = "recursive-depth";
    String RELEASE_CODENAME = "release-codename";
    String RELEASE_VERSION = "release-version";
    String RELOAD = "reload";
    String RELOAD_HOST = "reload-host";
    String RELOAD_REQUIRED = "reload-required";
    String RELOAD_SERVERS = "reload-servers";
    String REPLY_PROPERTIES = "reply-properties";
    String REQUEST_CONTROLLER = "request-controller";
    String REQUIRED = "required";
    String RESPONSE = "response";
    String RESOURCE_ADAPTERS = "resource-adapters";
    String RESPONSE_HEADERS = "response-headers";
    String RESTART = "restart";
    String RESTART_REQUIRED = "restart-required";
    String RESTART_SERVERS = "restart-servers";
    String RESULT = "result";
    String RESUME = "resume";
    String RESUME_SERVERS = "resume-servers";
    String ROLE_MAPPING = "role-mapping";
    String ROLES = "HEADER_ROLES";
    String RUNNING_MODE = "running-mode";
    String RUNTIME = "runtime";
    String RUNTIME_NAME = "runtime-name";

    String SECURITY = "security";
    String SECURITY_DOMAIN = "security-domain";
    String SELECT = "select";
    String SERVER = "server";
    String SERVER_CONFIG = "server-config";
    String SERVER_GROUP = "server-group";
    String SERVER_GROUP_SCOPED_ROLE = "server-group-scoped-role";
    String SERVER_GROUPS = "server-groups";
    String SERVER_STATE = "server-state";
    String SERVER_TYPE = "server-type";
    String SERVICE = "service";
    String SOCKET_BINDING = "socket-binding";
    String SOCKET_BINDING_DEFAULT_INTERFACE = "socket-binding-default-interface";
    String SOCKET_BINDING_GROUP = "socket-binding-group";
    String SOCKET_BINDING_PORT_OFFSET = "socket-binding-port-offset";
    String SHUTDOWN = "shutdown";
    String SMTP = "smtp";
    String STANDALONE = "standalone";
    String STANDARD_ROLE_NAMES = "standard-role-names";
    String START = "start";
    String START_SERVERS = "start-servers";
    String STATUS = "status";
    String STATISTICS_ENABLED = "statistics-enabled";
    String STEPS = "steps";
    String STORAGE = "storage";
    String STOP = "stop";
    String STOP_SERVERS = "stop-servers";
    String SUBDEPLOYMENT = "subdeployment";
    String SUBSYSTEM = "subsystem";
    String SUCCESS = "success";
    String SUSPEND = "suspend";
    String SUSPEND_SERVERS = "suspend-servers";
    String SUSPEND_STATE = "suspend-state";
    String SYSTEM_PROPERTY = "system-property";

    String TIMEOUT = "timeout";
    String TRANSACTIONS = "transactions";
    String TRIM_DESCRIPTIONS = "trim-descriptions";
    String TYPE = "type";

    String UNIT = "unit";
    String URL = "url";

    String VALUE = "value";
    String VALUE_TYPE = "value-type";
    String VERBOSE = "verbose";

    String WEBSERVICES = "webservices";
    String WHERE = "where";
    String WHOAMI = "whoami";
    String WRITE_ATTRIBUTE_OPERATION = "write-attribute";

    String UNDEFINE_ATTRIBUTE_OPERATION = "undefine-attribute";
    String UNDEFINED = "undefined";
    String UNDERTOW = "undertow";

    String XA_DATA_SOURCE = "xa-data-source";
}

