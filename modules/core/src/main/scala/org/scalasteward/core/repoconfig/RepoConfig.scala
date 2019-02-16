/*
 * Copyright 2018-2019 scala-steward contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.scalasteward.core.repoconfig

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import org.scalasteward.core.model.Update

final case class RepoConfig(
    allowedUpdates: Option[List[DependencyPattern]] = None,
    ignoredUpdates: Option[List[DependencyPattern]] = None
) {
  def keep(update: Update.Single): Boolean =
    isAllowed(update) && !isIgnored(update)

  private def isAllowed(update: Update.Single): Boolean = {
    val patterns = allowedUpdates.getOrElse(List.empty)
    val m = DependencyPattern.findMatch(patterns, update)
    m.byArtifactId.isEmpty || m.byVersion.nonEmpty
  }

  private def isIgnored(update: Update.Single): Boolean = {
    val patterns = ignoredUpdates.getOrElse(List.empty)
    val m = DependencyPattern.findMatch(patterns, update)
    m.byVersion.nonEmpty
  }
}

object RepoConfig {
  implicit val repoConfigDecoder: Decoder[RepoConfig] =
    deriveDecoder
}
