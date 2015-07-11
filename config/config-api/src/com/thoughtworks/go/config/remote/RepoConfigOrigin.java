/*************************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *************************GO-LICENSE-END***********************************/
package com.thoughtworks.go.config.remote;

import com.thoughtworks.go.config.materials.ScmMaterialConfig;
import com.thoughtworks.go.domain.materials.MaterialConfig;
import com.thoughtworks.go.util.StringUtil;

/**
 * @understands that configuration is defined in versioned source code repository at particular revision.
 */
public class RepoConfigOrigin implements ConfigOrigin {

    private ConfigRepoConfig configRepo;
    private String revision;

    public RepoConfigOrigin()
    {
    }
    public RepoConfigOrigin(ConfigRepoConfig configRepo,String revision)
    {
        this.configRepo = configRepo;
        this.revision = revision;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RepoConfigOrigin repoConfigOrigin = (RepoConfigOrigin) o;

        if (!revision.equals(repoConfigOrigin.revision)) {
            return false;
        }
        if (configRepo != null ? !configRepo.equals(repoConfigOrigin.configRepo) : repoConfigOrigin.configRepo != null) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return revision != null ? revision.hashCode() : 0;
    }

    @Override
    public boolean canEdit() {
        // if there will be implementation to edit repository then this
        // class should participate in providing info on how to do that
        return false;
    }

    @Override
    public boolean isLocal() {
        return false;
    }

    @Override
    public String displayName() {
        return configRepo.getMaterialConfig().getDisplayName() + " at " + revision;
    }
    public MaterialConfig getMaterial() {
        if(configRepo == null)
            return  null;
        return configRepo.getMaterialConfig();
    }

    public boolean isFromRevision(String revision) {
        return this.revision.equals(revision);
    }

    public void setConfigRepo(ConfigRepoConfig configRepo) {
        this.configRepo = configRepo;
    }
}
