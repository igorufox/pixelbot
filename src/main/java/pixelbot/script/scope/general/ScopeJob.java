package pixelbot.script.scope.general;

public class ScopeJob implements Comparable<ScopeJob> {
	private String id;
	private IScopeMap job;
	private Configuration params = null;

	public class Configuration {
		private IScopeMap config_params;

		protected Configuration(IScopeMap params) {
			this.config_params = params == null ? new JScopeMap() : params;
		}

		public IScopeMap getDefault() throws InterruptedException {
			IScopeObject result = ScopeJob.readProperty(this.config_params, "defaults");
			if (result == null || !(result instanceof IScopeMap))
				result = new JScopeMap();
			return (IScopeMap) result;
		}

		public IScopeList getConfig(IScopeMap params) throws InterruptedException {
			IScopeObject obj = this.config_params.get("config");
			if (obj != null && obj instanceof IScopeFunction) {
				return (IScopeList) ((IScopeFunction) obj).call(this.config_params, params);
			}
			return new JScopeList();
		}

		public String getName() throws InterruptedException {
			IScopeObject obj = ScopeJob.readProperty(this.config_params, "name");
			return obj == null ? null : obj.toString();
		}
	}

	public ScopeJob(String id, IScopeMap job) {
		this.id = id;
		this.job = job;
	}

	@Override
	public int compareTo(ScopeJob o) {
		try {
			return this.getName().compareTo(o.getName());
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void main(IScopeMap params) throws InterruptedException {
		((IScopeFunction) this.job.get("main")).call(this.job, params);
	}

	protected static IScopeObject readProperty(IScopeMap obj, String propName)
			throws InterruptedException {
		IScopeObject property = obj.get(propName);
		if (property instanceof IScopeFunction) {
			return ((IScopeFunction) property).call(obj);
		} else {
			return property;
		}
	}

	public Configuration params() {
		if (this.params == null) {
			try {
				this.params = new Configuration((IScopeMap) ScopeJob.readProperty(this.job,
						"params"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return this.params;
	}

	public String getShortName() throws InterruptedException {
		String result = null;
		if (this.params() != null) {
			result = this.params().getName();
		}
		if (result == null) {
			result = this.getId();
		}
		return result;
	}

	public String getName() throws InterruptedException {
		return this.getShortName() + "(" + this.job.origin() + ")";
	}

	public String getId() {
		return this.id;
	}

	@Override
	public String toString() {
		try {
			return this.getName();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ScopeJob) {
			ScopeJob oth = (ScopeJob) obj;
			return this.id.equals(oth.id);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

}
